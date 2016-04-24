package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.*;

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

    public void tagOverrides() {
        CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference()).getOverridingExecutable();
        if (reference != null) {
            ExecutableWrapper overridingMethod = getFactory().wrap(reference);
            getLogger().addTriple(this, Ontology.OVERRIDES_PROPERTY, overridingMethod);

            if (!overridingMethod.isDeclarationAvailable()) {
                overridingMethod.extract();
            }
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

    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
