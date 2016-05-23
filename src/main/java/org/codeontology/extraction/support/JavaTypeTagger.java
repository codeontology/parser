package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.declaration.TypeEntity;

public class JavaTypeTagger {

    private TypedElementEntity<?> typedElement;

    public JavaTypeTagger(TypedElementEntity<?> typedElement) {
        this.typedElement = typedElement;
    }

    public void tagJavaType() {
        TypeEntity<?> type = typedElement.getJavaType();
        if (type != null) {
            RDFLogger.getInstance().addTriple(typedElement, Ontology.JAVA_TYPE_PROPERTY, type);
            type.follow();
        }
    }
}
