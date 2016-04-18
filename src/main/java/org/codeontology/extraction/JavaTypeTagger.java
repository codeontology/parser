package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class JavaTypeTagger {

    private Wrapper<? extends CtTypedElement> typedElement;
    private TypeWrapper type;

    public JavaTypeTagger(Wrapper<? extends CtTypedElement> typedElement) {
        this.typedElement = typedElement;
        if (typedElement.getReference() instanceof CtVariableReference) {
            CtTypeReference reference = ((CtVariableReference) typedElement.getReference()).getType();
            type = WrapperFactory.getInstance().wrap(reference);
        } else {
            type = WrapperFactory.getInstance().wrap((CtTypeReference<?>) typedElement.getReference());
        }
    }

    private void tagJavaType() {
        RDFWriter.addTriple(typedElement, Ontology.JAVA_TYPE_PROPERTY, type);
        if (!type.isDeclarationAvailable()) {
            type.extract();
        }
    }

    protected void tagJavaType(ExecutableWrapper executable) {
        if (isTypeVariable()) {
            getTypeVariable().findAndSetParent(executable);
        }
        handleGenericArray(executable.getReference());
        tagJavaType();
    }

    protected void tagJavaType(LocalVariableWrapper variable) {
        if (isTypeVariable()) {
            getTypeVariable().findAndSetParent(variable.getParent());
        }
        handleGenericArray(variable.getParent().getReference());
        tagJavaType();
    }

    protected void tagJavaType(CtType type) {
        if (isTypeVariable()) {
            getTypeVariable().findAndSetParent(type);
        }
        handleGenericArray(type.getReference());
        tagJavaType();
    }

    private boolean isTypeVariable() {
        return type instanceof TypeVariableWrapper;
    }

    private TypeVariableWrapper getTypeVariable() {
        return (TypeVariableWrapper) type;
    }

    private void handleGenericArray(CtReference parent) {
        if (type instanceof ArrayWrapper) {
            ((ArrayWrapper) type).setParent(parent);
        }
    }


}
