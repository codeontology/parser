package org.codeontology.extraction;

import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ExecutableEntity<E extends CtExecutable<?> & CtTypeMember & CtGenericElement>
        extends NamedElementEntity<E> implements ModifiableEntity<E>, MemberEntity<E> {

    private Set<ExecutableEntity<?>> executables;
    private Set<TypeEntity<?>> requestedTypes;
    private Set<LambdaEntity> lambdas;
    private Set<AnonymousClassEntity> anonymousClasses;
    private Set<LocalVariableEntity> localVariables;
    private Set<FieldEntity> fields;
    private List<ParameterEntity> parameters;

    public ExecutableEntity(E executable) {
        super(executable);
        initSets();
    }

    public ExecutableEntity(CtExecutableReference<?> reference) {
        super(reference);
        initSets();
    }

    private void initSets() {
        executables = new HashSet<>();
        requestedTypes = new HashSet<>();
        lambdas = new HashSet<>();
        anonymousClasses = new HashSet<>();
        localVariables = new HashSet<>();
        fields = new HashSet<>();
    }

    @Override
    public String buildRelativeURI() {
        String uri = getReference().toString();
        uri = uri.replaceAll(", |#", SEPARATOR);
        return uri;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagDeclaringElement();
        tagParameters();
        tagModifiers();
        tagVarArgs();
        if (isDeclarationAvailable()) {
            processStatements();
            tagRequestedTypes();
            tagExecutables();
            tagRequestedFields();
            tagLocalVariables();
            tagLambdas();
            tagAnonymousClasses();
            tagAnnotations();
            tagComment();
            tagSourceCode();
            tagThrows();
        }
    }

    public Entity<?> getDeclaringElement() {
        CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
        return getFactory().wrap(reference.getDeclaringType());
    }

    public void tagDeclaringElement() {
        new DeclaringElementTagger(this).tagDeclaredBy();
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    public List<Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        } else {
            CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
            Executable executable = ReflectionFactory.getInstance().createActualExecutable(reference);
            if (executable != null) {
                int modifiersCode = executable.getModifiers();
                return Modifier.asList(modifiersCode);
            }
            return new ArrayList<>();
        }
    }

    public void tagParameters() {
        List<ParameterEntity> parameters = getParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            ParameterEntity parameter = parameters.get(i);
            parameter.setParent(this);
            parameter.setPosition(i);
            getLogger().addTriple(this, Ontology.PARAMETER_PROPERTY, parameter);
            parameter.extract();
        }
    }

    public List<ParameterEntity> getParameters() {
        if (parameters == null) {
            setParameters();
        }

        return parameters;
    }

    private void setParameters() {
        parameters = new ArrayList<>();
        if (isDeclarationAvailable()) {
            List<CtParameter<?>> parameterList = getElement().getParameters();
            for (CtParameter<?> current : parameterList) {
                parameters.add(getFactory().wrap(current));
            }
        } else {
            List<CtTypeReference<?>> references = ((CtExecutableReference<?>) getReference()).getParameters();
            for (CtTypeReference<?> reference : references) {
                parameters.add(getFactory().wrapByTypeReference(reference));
            }
        }
    }

    public void tagThrows() {
        Set<CtTypeReference<? extends Throwable>> thrownTypes = getElement().getThrownTypes();
        for (CtTypeReference<? extends Throwable> current : thrownTypes) {
            TypeEntity<?> thrownType = getFactory().wrap(current);
            thrownType.setParent(this);
            getLogger().addTriple(this, Ontology.THROWS_PROPERTY, thrownType);
        }
    }

    protected void processStatements() {
        addRequestedTypes(new HashSet<>(getElement().getThrownTypes()));

        CtExecutable executable = getElement();
        CtBlock<?> body = executable.getBody();

        List<CtStatement> statements;
        try {
            statements = body.getStatements();
        } catch (NullPointerException e) {
            return;
        }

        int size = statements.size();
        for (int i = 0; i < size; i++) {
            CtStatement statement = statements.get(i);
            if (createsAnonymousClass(statement) || statement instanceof CtClass) {
                addAnonymousClasses(statement);
            } else {
                addRequestedTypes(statement.getReferencedTypes());
                addInvocations(statement);
                addRequestedFields(statement);
                addLocalVariables(statement);
                addLambdas(statement);
            }
            if (statement instanceof CtReturn<?>) {
                tagReturnsVariable((CtReturn<?>) statement);
            }

            if (CodeOntology.processStatements()) {
                StatementEntity<?> entity = getFactory().wrap(statement);
                entity.setPosition(i);
                entity.setParent(this);
                entity.extract();
            }
        }

    }

    public void addAnonymousClasses(CtStatement statement) {
        List<CtNewClass<?>> newClasses = statement.getElements(element -> element != null);
        for (CtNewClass<?> newClass : newClasses) {
            AnonymousClassEntity<?> anonymousClass = new AnonymousClassEntity<>(newClass.getAnonymousClass());
            anonymousClass.setParent(this);
            anonymousClasses.add(anonymousClass);
        }
    }

    public void tagAnonymousClasses() {
        for (AnonymousClassEntity<?> anonymousClass : anonymousClasses) {
            getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, anonymousClass);
            anonymousClass.extract();
            Set<Entity<?>> requestedResources = anonymousClass.getRequestedResources();
            for (Entity<?> resource : requestedResources) {
                tagRequests(resource);
            }
        }
    }

    public void addInvocations(CtStatement statement) {
        List<CtExecutableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtLambda<?>) {
                LambdaEntity lambda = getFactory().wrap((CtLambda<?>) executable);
                lambda.setParent(this);
                lambdas.add(lambda);
            } else if (!(reference.getParent() instanceof CtExecutableReferenceExpression<?, ?>)) {
                executables.add(getFactory().wrap(reference));
            }
        }
    }

    public void tagLambdas() {
        for (LambdaEntity lambda : lambdas) {
            tagRequests(lambda);
            lambda.extract();
        }
    }

    public void addLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            LambdaEntity lambdaEntity =  getFactory().wrap(lambda);
            lambdaEntity.setParent(this);
            this.lambdas.add(lambdaEntity);
        }
    }

    public void addRequestedFields(CtStatement statement) {
        List<CtFieldReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class));

        for (CtFieldReference<?> currentReference : references) {
            fields.add(getFactory().wrap(currentReference));
        }
    }

    public void tagRequestedFields() {
        for (FieldEntity field : fields) {
            tagRequests(field);
            field.follow();
        }
    }

    public void addRequestedTypes(Set<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            if (!(reference instanceof CtImplicitTypeReference<?>)) {
                TypeEntity<?> type = getFactory().wrap(reference);
                if (type != null) {
                    type.setParent(this);
                    requestedTypes.add(type);
                }
            }
        }
    }

    public void tagRequestedTypes() {
        for (TypeEntity<?> type : requestedTypes) {
            tagRequests(type);
            type.follow();
        }
    }


    private boolean createsAnonymousClass(CtStatement statement) {
        List<CtNewClass> newClasses = statement.getElements(element -> element != null);
        return !newClasses.isEmpty();
    }

    public void addLocalVariables(CtStatement statement) {
        List<CtLocalVariableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtLocalVariableReferenceImpl.class));

        for (CtLocalVariableReference<?> reference : references) {
            CtLocalVariable variable = reference.getDeclaration();
            if (variable != null) {
                LocalVariableEntity localVariable = getFactory().wrap(variable);
                localVariable.setParent(this);
                localVariables.add(localVariable);
            }
        }
    }

    public void tagLocalVariables() {
        for (LocalVariableEntity variable : localVariables) {
            tagRequests(variable);
            variable.extract();
        }
    }

    public void tagExecutables() {
        for (ExecutableEntity<?> executable : executables) {
            tagRequests(executable);
            if (executable instanceof ConstructorEntity) {
                tagConstructs(executable);
            }
            executable.follow();
        }
    }

    public void tagConstructs(ExecutableEntity<?> executable) {
        Entity<?> declaringType = executable.getDeclaringElement();
        getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, declaringType);
        declaringType.follow();
    }

    public void tagRequests(Entity<?> requested) {
        getLogger().addTriple(this, Ontology.REQUESTS_PROPERTY, requested);
    }

    public void tagReturnsVariable(CtReturn<?> returnStatement) {
        CtExpression<?> returned = returnStatement.getReturnedExpression();
        if (returned instanceof CtVariableAccess<?>) {
            CtVariableReference<?> reference = ((CtVariableAccess<?>) returned).getVariable();
            if (reference instanceof CtFieldReference<?>) {
                CtField<?> field = ((CtFieldReference<?>) reference).getDeclaration();
                tagReturnsField(field);
            } else if (reference instanceof CtLocalVariableReference<?>) {
                CtLocalVariable<?> variable = ((CtLocalVariableReference<?>) reference).getDeclaration();
                tagReturnsLocalVariable(variable);
            }
        }
    }

    public void tagReturnsLocalVariable(CtLocalVariable<?> variable) {
        LocalVariableEntity entity = getFactory().wrap(variable);
        entity.setParent(this);
        getLogger().addTriple(this, Ontology.RETURNS_VAR_PROPERTY, entity);
    }

    public void tagReturnsField(CtField<?> field) {
        if (field != null) {
            Entity entity =  getFactory().wrap(field);
            getLogger().addTriple(this, Ontology.RETURNS_FIELD_PROPERTY, entity);
        }
    }

    public List<Entity<?>> getRequestedResources() {
        List<Entity<?>> requestedResources = new ArrayList<>();

        requestedResources.addAll(executables);
        requestedResources.addAll(fields);
        requestedResources.addAll(requestedTypes);

        return requestedResources;
    }

    public void tagVarArgs() {
        List<ParameterEntity> parameters = getParameters();
        int size = parameters.size();
        boolean value = false;
        if (size != 0) {
            ParameterEntity last = parameters.get(size - 1);
            if (last.isDeclarationAvailable()) {
                value = last.getElement().isVarArgs();
            } else {
                CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
                Executable executable = ReflectionFactory.getInstance().createActualExecutable(reference);
                if (executable != null) {
                    value = executable.isVarArgs();
                }
            }
        }
        getLogger().addTriple(this, Ontology.VAR_ARGS_PROPERTY, getModel().createTypedLiteral(value));
    }
}
