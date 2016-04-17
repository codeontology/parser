package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class MethodWrapper extends ExecutableWrapper<CtMethod<?>> {
    public MethodWrapper(CtMethod<?> method) {
        super(method);
    }

    public MethodWrapper(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_CLASS;
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
        if (isDeclarationAvailable()) {
            tagFormalTypeParameters();
        }
    }

    protected void tagReturns() {
        if (isDeclarationAvailable()) {
            tagReturnsByReference();
        } else {
            tagReturnsByReflection();
        }
    }

    private void tagReturnsByReference() {
        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        Wrapper returnType = getFactory().wrap(reference);
        if (reference instanceof CtTypeParameterReference) {
            ((TypeVariableWrapper) returnType).findAndSetParent(this);
        } else if (reference.getDeclaration() == null) {
            returnType.extract();
        }
        RDFWriter.addTriple(this, Ontology.RETURNS_PROPERTY, returnType);
    }

    private void tagReturnsByReflection() {
        CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
        try {
            Method method = reference.getActualMethod();
            Type returnType = method.getGenericReturnType();
            String name = returnType.getTypeName();
            name = name.replaceAll("<|>", SEPARATOR);
            RDFWriter.addTriple(this, Ontology.RETURNS_PROPERTY, getModel().getResource(Ontology.WOC + name));
            Wrapper wrapper = getFactory().wrap(reference.getType());
            if (wrapper != null && !wrapper.isDeclarationAvailable()) {
                wrapper.extract();
            }
        } catch (SpoonClassNotFoundException | NullPointerException e) {
            tagReturnsByReference();
        }
    }

    protected void tagFormalTypeParameters() {
        List<CtTypeReference<?>> parameters = getElement().getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableWrapper wrapper = ((TypeVariableWrapper) getFactory().wrap(parameters.get(i)));
            wrapper.setParent(this);
            wrapper.setPosition(i);
            RDFWriter.addTriple(this, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, wrapper);
            wrapper.extract();
        }
    }
}