package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.*;
import java.util.List;

public class MethodWrapper extends ExecutableWrapper<CtMethod<?>> implements GenericDeclarationWrapper<CtMethod<?>> {
    public MethodWrapper(CtMethod<?> method) {
        super(method);
    }

    public MethodWrapper(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_ENTITY;
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

    public void tagOverrides() {
        try {
            CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference()).getOverridingExecutable();
            if (reference != null) {
                ExecutableWrapper overridingMethod = getFactory().wrap(reference);
                getLogger().addTriple(this, Ontology.OVERRIDES_PROPERTY, overridingMethod);
                overridingMethod.follow();
            }
        } catch (Exception | Error e) {
            // could not get overriding executable
            // we just skip this method
        }
    }

    public void tagReturns() {
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
        returnType.follow();

        return returnType;
    }

    private TypeWrapper getGenericReturnType() {
        TypeWrapper<?> result = null;
        if (!isDeclarationAvailable()) {
            try {
                CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
                Method method = (Method) ReflectionFactory.getInstance().createActualExecutable(reference);
                Type returnType = method.getGenericReturnType();

                if (returnType instanceof GenericArrayType ||
                    returnType instanceof TypeVariable<?>  ||
                    returnType instanceof ParameterizedType) {

                    result = getFactory().wrap(returnType);
                    result.setParent(this);
                }

            } catch (Throwable t) {
                return null;
            }
        }

        return result;
    }

    @Override
    public List<TypeVariableWrapper> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    @Override
    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
