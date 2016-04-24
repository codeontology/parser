package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ExecutableWrapper<E extends CtExecutable<?> & CtTypeMember & CtGenericElement> extends Wrapper<E> {


    public ExecutableWrapper(E executable) {
        super(executable);
    }

    public ExecutableWrapper(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected String getRelativeURI() {
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
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagComment();
            tagSourceCode();
            tagModifiers();
            tagThrows();
            processStatements();
        }
    }

    protected void tagDeclaringType() {
        new DeclaredByTagger(this).tagDeclaredBy();
    }

    protected void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    protected void tagParameters() {
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

    protected void tagThrows() {
        Set<CtTypeReference<? extends Throwable>> thrownTypes = getElement().getThrownTypes();
        for (CtTypeReference<? extends Throwable> current : thrownTypes) {
            TypeWrapper<?> thrownType = getFactory().wrap(current);
            thrownType.setParent(this);
            getLogger().addTriple(this, Ontology.THROWS_PROPERTY, thrownType);
        }
    }

    protected void processStatements() {
        Set<CtTypeReference<?>> types = new HashSet<>();
        types.addAll(getElement().getThrownTypes());

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
                types.addAll(statement.getReferencedTypes());
                tagInvocations(statement);
                tagRequestedFields(statement);
                tagLocalVariables(statement);
                tagLambdas(statement);

                if (statement instanceof CtReturn<?>) {
                    tagReturnsVariable((CtReturn<?>) statement);
                }
            } catch (RuntimeException e) {
                if (!createsAnonymousClass(statement)) {
                    throw e;
                }
            }
        }

        tagRequestedTypes(types);
    }

    protected void tagInvocations(CtStatement statement) {
        Set<CtExecutableReference<?>> references = new HashSet<>(statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class)));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtLambda<?>) {
                tagLambdaRequested((CtLambda<?>) executable);
            } else {
                tagExecutableRequested(reference);
            }
        }
    }

    private void tagLambdaRequested(CtLambda<?> lambda) {
        LambdaWrapper wrapper = getFactory().wrap(lambda);
        wrapper.setParent(this);
        tagRequests(wrapper.getResource());
        wrapper.extract();
    }

    private void tagLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            tagLambdaRequested(lambda);
        }
    }

    protected void tagRequestedFields(CtStatement statement) {
        Set<CtFieldReference<?>> references = new HashSet<>(statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class)));

        for (CtFieldReference<?> currentReference : references) {
            CtField<?> field = currentReference.getDeclaration();
            if (field != null) {
                tagRequests(getFactory().wrap(field).getResource());
            }
        }
    }

    protected void tagRequestedTypes(Collection<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            TypeWrapper<?> type = getFactory().wrap(reference);
            if (type != null) {
                type.setParent(this);
                if (!type.isDeclarationAvailable()) {
                    type.extract();
                }
                tagRequests(type.getResource());
            }
        }
    }

    private boolean createsAnonymousClass(CtStatement statement) {
        List<CtNewClass> newClasses = statement.getElements(element -> element != null);
        return !newClasses.isEmpty();
    }

    private void tagLocalVariables(CtStatement statement) {
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

    protected void tagExecutableRequested(CtExecutableReference<?> reference) {
        ExecutableWrapper<?> executable = getFactory().wrap(reference);
        tagRequests(executable.getResource());
        if (reference.isConstructor()) {
            tagConstructs(reference);
        }
        if (!executable.isDeclarationAvailable()) {
            executable.extract();
        }
    }

    protected void tagConstructs(CtExecutableReference<?> reference) {
        Wrapper<?> declaringType = getFactory().wrap(reference.getDeclaringType());
        getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, declaringType);
        if (!declaringType.isDeclarationAvailable()) {
            declaringType.extract();
        }
    }

    protected void tagRequests(RDFNode node) {
        getLogger().addTriple(this, Ontology.REQUESTS_PROPERTY, node);
    }

    protected void tagReturnsVariable(CtReturn<?> returnStatement) {
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

    protected void tagReturnsLocalVariable(CtLocalVariable<?> variable) {
        LocalVariableWrapper wrapper = getFactory().wrap(variable);
        wrapper.setParent(this);
        getLogger().addTriple(this, Ontology.RETURNS_VAR_PROPERTY, wrapper.getResource());
        wrapper.extract();
    }

    protected void tagReturnsField(CtField<?> field) {
        if (field != null) {
            Wrapper wrapper =  getFactory().wrap(field);
            getLogger().addTriple(this, Ontology.RETURNS_FIELD_PROPERTY, wrapper.getResource());
        }
    }

}
