package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
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
import java.util.*;

public abstract class ExecutableWrapper<E extends CtExecutable<?> & CtTypeMember & CtGenericElement> extends Wrapper<E> implements ModifiableWrapper {

    public ExecutableWrapper(E executable) {
        super(executable);
    }

    public ExecutableWrapper(CtExecutableReference<?> reference) {
        super(reference);
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
        tagDeclaringType();
        tagParameters();
        tagModifiers();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagComment();
            tagSourceCode();
            tagThrows();
            processStatements();
        }
    }

    public void tagDeclaringType() {
        new DeclaredByTagger(this).tagDeclaredBy();
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
        if (isDeclarationAvailable()) {
            List<CtParameter<?>> parameters = getElement().getParameters();
            int parametersNumber = parameters.size();

            for (int i = 0; i < parametersNumber; i++) {
                ParameterWrapper parameterWrapper = getFactory().wrap(parameters.get(i));
                parameterWrapper.setParent(this);
                parameterWrapper.setPosition(i);
                getLogger().addTriple(this, Ontology.PARAMETER_PROPERTY, parameterWrapper);
                parameterWrapper.extract();
            }

        } else {
            List<CtTypeReference<?>> parameters = ((CtExecutableReference<?>) getReference()).getParameters();
            int parametersNumber = parameters.size();

            for (int i = 0; i < parametersNumber; i++) {
                ParameterWrapper parameterWrapper = getFactory().wrapByTypeReference(parameters.get(i));
                if (parameterWrapper != null) {
                    parameterWrapper.setParent(this);
                    parameterWrapper.setPosition(i);
                    getLogger().addTriple(this, Ontology.PARAMETER_PROPERTY, parameterWrapper);
                    parameterWrapper.extract();
                }
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
        tagRequestedTypes(new HashSet<>(getElement().getThrownTypes()));

        CtExecutable executable = getElement();
        CtBlock<?> body = executable.getBody();

        List<CtStatement> statements;
        try {
            statements = body.getStatements();
        } catch (NullPointerException e) {
            return;
        }

        for (CtStatement statement : statements) {
            try {
                tagRequestedTypes(statement.getReferencedTypes());
                tagInvocations(statement);
                tagRequestedFields(statement);
                tagLocalVariables(statement);
                tagLambdas(statement);
                tagAnonymousClasses(statement);

                if (statement instanceof CtReturn<?>) {
                    tagReturnsVariable((CtReturn<?>) statement);
                }

            } catch (RuntimeException e) {
                if (!createsAnonymousClass(statement) && !(statement instanceof CtClass<?>)) {
                    throw e;
                }
            }
        }

    }

    public void tagAnonymousClasses(CtStatement statement) {
        List<CtNewClass<?>> newClasses = statement.getElements(element -> element != null);
        for (CtNewClass<?> newClass : newClasses) {
            AnonymousClassWrapper<?> anonymousClass = new AnonymousClassWrapper<>(newClass.getAnonymousClass());
            anonymousClass.setParent(this);
            getLogger().addTriple(this, Ontology.ANONYMOUS_CLASS_PROPERTY, anonymousClass);
            anonymousClass.extract();
        }
    }

    public void tagInvocations(CtStatement statement) {
        List<CtExecutableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtLambda<?>) {
                tagLambdaRequested((CtLambda<?>) executable);
            } else if (!(reference.getParent() instanceof CtExecutableReferenceExpression<?, ?>)) {
                tagExecutableRequested(reference);
            }
        }
    }

    public void tagLambdaRequested(CtLambda<?> lambda) {
        LambdaWrapper wrapper = getFactory().wrap(lambda);
        wrapper.setParent(this);
        tagRequests(wrapper.getResource());
        wrapper.extract();
    }

    public void tagLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            tagLambdaRequested(lambda);
        }
    }

    public void tagRequestedFields(CtStatement statement) {
        Set<CtFieldReference<?>> references = new HashSet<>(statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class)));

        for (CtFieldReference<?> currentReference : references) {
            CtField<?> field = currentReference.getDeclaration();
            if (field != null) {
                tagRequests(getFactory().wrap(field).getResource());
            }
        }
    }

    public void tagRequestedTypes(Collection<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            if (!(reference instanceof CtImplicitTypeReference<?>)) {
                TypeWrapper<?> type = getFactory().wrap(reference);
                if (type != null) {
                    type.setParent(this);
                    type.follow();
                    tagRequests(type.getResource());
                }
            }
        }
    }

    private boolean createsAnonymousClass(CtStatement statement) {
        List<CtNewClass> newClasses = statement.getElements(element -> element != null);
        return !newClasses.isEmpty();
    }

    public void tagLocalVariables(CtStatement statement) {
        List<CtLocalVariableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtLocalVariableReferenceImpl.class));

        for (CtLocalVariableReference<?> reference : references) {
            CtLocalVariable variable = reference.getDeclaration();
            if (variable != null) {
                LocalVariableWrapper wrapper = getFactory().wrap(variable);
                wrapper.setParent(this);
                wrapper.extract();
            }
        }
    }

    public void tagExecutableRequested(CtExecutableReference<?> reference) {
        ExecutableWrapper<?> executable = getFactory().wrap(reference);
        tagRequests(executable.getResource());
        if (reference.isConstructor()) {
            tagConstructs(reference);
        }
        executable.follow();
    }

    public void tagConstructs(CtExecutableReference<?> reference) {
        Wrapper<?> declaringType = getFactory().wrap(reference.getDeclaringType());
        getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, declaringType);
        declaringType.follow();
    }

    public void tagRequests(RDFNode node) {
        getLogger().addTriple(this, Ontology.REQUESTS_PROPERTY, node);
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

}
