package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
        getLogger().addTriple(this, Ontology.RETURNS_PROPERTY, getReturnType());
    }

    private TypeWrapper getReturnType() {
        TypeWrapper returnType = getGenericReturnType();
        if (returnType != null) {
            return returnType;
        }

        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        returnType = getFactory().wrap(reference);
        if (returnType instanceof TypeVariableWrapper) {
            ((TypeVariableWrapper) returnType).findAndSetParent(this);
        } else if (returnType instanceof ArrayWrapper) {
            ((ArrayWrapper) returnType).setParent(getReference());
        } else if (returnType instanceof ParameterizedTypeWrapper) {
            ((ParameterizedTypeWrapper) returnType).setParent(getReference());
            returnType.extract();
        } else if (reference.getDeclaration() == null) {
            returnType.extract();
        }

        return returnType;
    }

    private TypeWrapper getGenericReturnType() {
        TypeWrapper result = null;
        if (!isDeclarationAvailable()) {
            try {
                CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
                Method method = reference.getActualMethod();
                Type returnType = method.getGenericReturnType();

                if (returnType instanceof GenericArrayType) {
                    ArrayWrapper arrayType = getFactory().wrap((GenericArrayType) returnType);
                    arrayType.setParent(this.getReference());
                    result = arrayType;
                } else if (returnType instanceof TypeVariable) {
                    TypeVariableWrapper typeVariable = getFactory().wrap((TypeVariable) returnType);
                    typeVariable.findAndSetParent(this);
                    result = typeVariable;
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