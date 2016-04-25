package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class JavaTypeTagger {

    private Wrapper<? extends CtTypedElement> typedElement;
    private TypeWrapper<?> type;

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
        RDFLogger.getInstance().addTriple(typedElement, Ontology.JAVA_TYPE_PROPERTY, type);
        type.follow();
    }

    public void tagJavaType(Wrapper<?> parent) {
        type.setParent(parent);
        tagJavaType();
    }
}
