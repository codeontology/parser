package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ExecutableExtractor<E extends CtExecutable<?> & CtTypeMember & CtGenericElement> extends TypeMemberExtractor<E> {
    
    public ExecutableExtractor(E executable) {
        super(executable);
    }

    public ExecutableExtractor(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagDeclaringType();
        tagParameters();
        if (isDeclarationAvailable()) {
            tagComment();
            tagSourceCode();
            tagEncapsulation();
            tagModifier();
            tagThrows();
            processStatements();
        }
    }

    protected void tagParameters() {
        if (isDeclarationAvailable()) {
            List<CtParameter<?>> parameters = getElement().getParameters();
            int parametersNumber = parameters.size();

            for (int i = 0; i < parametersNumber; i++) {
                ParameterExtractor parameterWrapper = getFactory().getExtractor(parameters.get(i));
                parameterWrapper.setParent(this);
                parameterWrapper.setPosition(i);
                addStatement(Ontology.getParameterProperty(), parameterWrapper.getResource());
                parameterWrapper.extract();
            }

        } else {
            List<CtTypeReference<?>> parameters = ((CtExecutableReference<?>) getReference()).getParameters();
            int parametersNumber = parameters.size();

            for (int i = 0; i < parametersNumber; i++) {
                ParameterExtractor parameterWrapper = getFactory().getExtractorByTypeReference(parameters.get(i));
                if (parameterWrapper != null) {
                    parameterWrapper.setParent(this);
                    parameterWrapper.setPosition(i);
                    addStatement(Ontology.getParameterProperty(), parameterWrapper.getResource());
                    parameterWrapper.extract();
                }
            }
        }
    }

    protected void tagThrows() {
        Set<CtTypeReference<? extends Throwable>> thrownTypes = getElement().getThrownTypes();
        for (CtTypeReference<? extends Throwable> current : thrownTypes) {
            Resource thrownTypeResource = getFactory().getExtractor(current).getResource();
            addStatement(Ontology.getThrowsProperty(), thrownTypeResource);
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
            types.addAll(statement.getReferencedTypes());
            tagInvocations(statement);
            tagRequestedFields(statement);
            tagLocalVariables(statement);
            tagLambdas(statement);

            if (statement instanceof CtReturn<?>) {
                tagReturnsVariable((CtReturn<?>) statement);
            }
        }

        tagRequestedTypes(types);
    }

    protected void tagInvocations(CtStatement statement) {
        Set<CtExecutableReference<?>> references = new HashSet<>(statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class)));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtMethod<?>) {
                tagMethodRequested((CtMethod<?>) executable);
            } else if (executable instanceof CtConstructor<?>) {
                tagConstructs((CtConstructor<?>) executable);
            } else if (executable instanceof CtLambda<?>) {
                tagLambdaRequested((CtLambda<?>) executable);
            } else if (executable == null) {
                tagExternalExecutableRequested(reference);
            }
        }
    }

    private void tagLambdaRequested(CtLambda<?> lambda) {
        System.out.println("found lambda");
        LambdaExtractor extractor = getFactory().getExtractor(lambda);
        extractor.setParent(this);
        tagRequests(extractor.getResource());
        extractor.extract();
    }

    private void tagLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            tagLambdaRequested(lambda);
        }
    }


    private void tagExternalExecutableRequested(CtExecutableReference<?> reference) {
        Extractor extractor = getFactory().getExtractor(reference);
        if (reference.isConstructor()) {
            tagConstructs(reference);
        } else {
            tagRequests(extractor.getResource());
        }
        if (reference.getDeclaration() == null) {
            extractor.extract();
        }
    }

    protected void tagRequestedFields(CtStatement statement) {
        Set<CtFieldReference<?>> references = new HashSet<>(statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class)));

        for (CtFieldReference<?> currentReference : references) {
            CtField<?> field = currentReference.getDeclaration();
            if (field != null) {
                tagRequests(getFactory().getExtractor(field).getResource());
            }
        }
    }

    protected void tagRequestedTypes(Collection<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            TypeExtractor extractor = getFactory().getExtractor(reference);
            if (extractor != null) {
                tagRequests(extractor.getResource());
                if (reference.getDeclaration() == null) {
                    extractor.extract();
                }
            }
        }
    }

    private void tagLocalVariables(CtStatement statement) {
        List<CtLocalVariableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtLocalVariableReferenceImpl.class));

        for (CtLocalVariableReference<?> reference : references) {
            CtLocalVariable variable = reference.getDeclaration();
            if (variable != null) {
                LocalVariableExtractor wrapper = getFactory().getExtractor(variable);
                wrapper.setParent(this);
                wrapper.extract();
            }
        }

    }

    protected void tagMethodRequested(CtMethod<?> method) {
        tagRequests(getFactory().getExtractor(method).getResource());
    }

    protected void tagConstructs(CtConstructor<?> executable) {
        Resource constructed = getFactory().getExtractor(executable).getResource();
        addStatement(Ontology.getConstructsProperty(), constructed);
    }

    protected void tagConstructs(CtExecutableReference<?> reference) {
        Extractor<?> extractor = getFactory().getExtractor(reference);
        addStatement(Ontology.getConstructsProperty(), extractor.getResource());
        if (reference.getDeclaration() == null) {
            extractor.extract();
        }
    }

    protected void tagRequests(RDFNode node) {
        addStatement(Ontology.getRequestsProperty(), node);
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
        LocalVariableExtractor wrapper = getFactory().getExtractor(variable);
        wrapper.setParent(this);
        addStatement(Ontology.getReturnLocalFieldProperty(), wrapper.getResource());
    }

    protected void tagReturnsField(CtField<?> field) {
        if (field != null) {
            Extractor extractor =  getFactory().getExtractor(field);
            addStatement(Ontology.getReturnClassFieldProperty(), extractor.getResource());
        }
    }

}
