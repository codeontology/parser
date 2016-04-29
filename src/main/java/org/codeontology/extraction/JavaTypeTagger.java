package org.codeontology.extraction;

import org.codeontology.Ontology;

public class JavaTypeTagger {

    private TypedElementWrapper<?> typedElement;
    private TypeWrapper<?> type;

    public JavaTypeTagger(TypedElementWrapper<?> typedElement) {
        this.typedElement = typedElement;
        this.type = typedElement.getJavaType();
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
