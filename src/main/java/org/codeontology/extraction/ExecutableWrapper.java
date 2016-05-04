package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.reflect.reference.*;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ExecutableWrapper<E extends CtExecutable<?> & CtTypeMember & CtGenericElement>
        extends AbstractWrapper<E> implements ModifiableWrapper<E>, MemberWrapper<E> {

    private Set<ExecutableWrapper<?>> executables;
    private Set<TypeWrapper<?>> requestedTypes;
    private Set<LambdaWrapper> lambdas;
    private Set<AnonymousClassWrapper> anonymousClasses;
    private Set<LocalVariableWrapper> localVariables;
    private Set<FieldWrapper> fields;
    private List<ParameterWrapper> parameters;

    public ExecutableWrapper(E executable) {
        super(executable);
        initSets();
    }

    public ExecutableWrapper(CtExecutableReference<?> reference) {
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

    public Wrapper<?> getDeclaringElement() {
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
        List<ParameterWrapper> parameters = getParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            ParameterWrapper parameter = parameters.get(i);
            parameter.setParent(this);
            parameter.setPosition(i);
            getLogger().addTriple(this, Ontology.PARAMETER_PROPERTY, parameter);
            parameter.extract();
        }
    }

    public List<ParameterWrapper> getParameters() {
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
            TypeWrapper<?> thrownType = getFactory().wrap(current);
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

        for (CtStatement statement : statements) {
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
        }

    }

    public void addAnonymousClasses(CtStatement statement) {
        List<CtNewClass<?>> newClasses = statement.getElements(element -> element != null);
        for (CtNewClass<?> newClass : newClasses) {
            AnonymousClassWrapper<?> anonymousClass = new AnonymousClassWrapper<>(newClass.getAnonymousClass());
            anonymousClass.setParent(this);
            anonymousClasses.add(anonymousClass);
        }
    }

    public void tagAnonymousClasses() {
        for (AnonymousClassWrapper<?> anonymousClass : anonymousClasses) {
            getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, anonymousClass);
            anonymousClass.extract();
            Set<Wrapper<?>> requestedResources = anonymousClass.getRequestedResources();
            for (Wrapper<?> resource : requestedResources) {
                tagRequests(resource);
            }
        }
    }

    public void addInvocations(CtStatement statement) {
        List<CtExecutableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtLambda<?>) {
                LambdaWrapper lambda = getFactory().wrap((CtLambda<?>) executable);
                lambda.setParent(this);
                lambdas.add(lambda);
            } else if (!(reference.getParent() instanceof CtExecutableReferenceExpression<?, ?>)) {
                executables.add(getFactory().wrap(reference));
            }
        }
    }

    public void tagLambdas() {
        for (LambdaWrapper lambda : lambdas) {
            tagRequests(lambda);
            lambda.extract();
        }
    }

    public void addLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            LambdaWrapper lambdaWrapper =  getFactory().wrap(lambda);
            lambdaWrapper.setParent(this);
            this.lambdas.add(lambdaWrapper);
        }
    }

    public void addRequestedFields(CtStatement statement) {
        List<CtFieldReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class));

        for (CtFieldReference<?> currentReference : references) {
            fields.add(getFactory().wrap(currentReference));
        }
    }

    public void tagRequestedFields() {
        for (FieldWrapper field : fields) {
            tagRequests(field);
            field.follow();
        }
    }

    public void addRequestedTypes(Set<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            if (!(reference instanceof CtImplicitTypeReference<?>)) {
                TypeWrapper<?> type = getFactory().wrap(reference);
                if (type != null) {
                    type.setParent(this);
                    requestedTypes.add(type);
                }
            }
        }
    }

    public void tagRequestedTypes() {
        for (TypeWrapper<?> type : requestedTypes) {
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
                LocalVariableWrapper localVariable = getFactory().wrap(variable);
                localVariable.setParent(this);
                localVariables.add(localVariable);
            }
        }
    }

    public void tagLocalVariables() {
        for (LocalVariableWrapper variable : localVariables) {
            tagRequests(variable);
            variable.extract();
        }
    }

    public void tagExecutables() {
        for (ExecutableWrapper<?> executable : executables) {
            tagRequests(executable);
            if (executable instanceof ConstructorWrapper) {
                tagConstructs(executable);
            }
            executable.follow();
        }
    }

    public void tagConstructs(ExecutableWrapper<?> executable) {
        Wrapper<?> declaringType = executable.getDeclaringElement();
        getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, declaringType);
        declaringType.follow();
    }

    public void tagRequests(Wrapper<?> requested) {
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
        LocalVariableWrapper wrapper = getFactory().wrap(variable);
        wrapper.setParent(this);
        getLogger().addTriple(this, Ontology.RETURNS_VAR_PROPERTY, wrapper);
    }

    public void tagReturnsField(CtField<?> field) {
        if (field != null) {
            Wrapper wrapper =  getFactory().wrap(field);
            getLogger().addTriple(this, Ontology.RETURNS_FIELD_PROPERTY, wrapper);
        }
    }

    public List<Wrapper<?>> getRequestedResources() {
        List<Wrapper<?>> requestedResources = new ArrayList<>();

        requestedResources.addAll(executables);
        requestedResources.addAll(fields);
        requestedResources.addAll(requestedTypes);

        return requestedResources;
    }

    public void tagVarArgs() {
        List<ParameterWrapper> parameters = getParameters();
        int size = parameters.size();
        boolean value = false;
        if (size != 0) {
            ParameterWrapper last = parameters.get(size - 1);
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
