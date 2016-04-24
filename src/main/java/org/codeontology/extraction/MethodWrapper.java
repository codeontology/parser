package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.*;
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
            tagOverrides();
            tagFormalTypeParameters();
        }
    }

    protected void tagOverrides() {
        CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference()).getOverridingExecutable();
        if (reference != null) {
            ExecutableWrapper overridingMethod = getFactory().wrap(reference);
            getLogger().addTriple(this, Ontology.OVERRIDES_PROPERTY, overridingMethod);

            if (!overridingMethod.isDeclarationAvailable()) {
                overridingMethod.extract();
            }
        }
    }

    protected void tagReturns() {
        getLogger().addTriple(this, Ontology.RETURNS_PROPERTY, getReturnType());
    }

    private TypeWrapper getReturnType() {
        TypeWrapper<?> returnType = getGenericReturnType();
        if (returnType != null) {
            return returnType;
        }

        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        returnType = getFactory().wrap(reference);
        returnType.setParent(this);
        if (!returnType.isDeclarationAvailable()) {
            returnType.extract();
        }

        return returnType;
    }

    private TypeWrapper getGenericReturnType() {
        TypeWrapper<?> result = null;
        if (!isDeclarationAvailable()) {
            try {
                CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
                Method method = reference.getActualMethod();
                Type returnType = method.getGenericReturnType();


                if (returnType instanceof GenericArrayType ||
                    returnType instanceof TypeVariable<?>  ||
                    returnType instanceof ParameterizedType) {

                    result = getFactory().wrap(returnType);
                    result.setParent(this);
                }
            } catch (SpoonClassNotFoundException | NullPointerException e) {
                return null;
            }
        }

        return result;
    }

    protected void tagFormalTypeParameters() {
        List<CtTypeReference<?>> parameters = getElement().getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableWrapper wrapper = ((TypeVariableWrapper) getFactory().wrap(parameters.get(i)));
            wrapper.setParent(this);
            wrapper.setPosition(i);
            getLogger().addTriple(this, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, wrapper);
            wrapper.extract();
        }
    }
}
