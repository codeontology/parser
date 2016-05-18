package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public abstract class CodeElementEntity<E extends CtElement> extends AbstractEntity<E> {

    CodeElementEntity() {

    }

    CodeElementEntity(E element) {
        super(element);
    }

    public void tagComment() {
        String comment = getElement().getDocComment();
        if (comment != null) {
            getLogger().addTriple(this, Ontology.COMMENT_PROPERTY, getModel().createLiteral(comment));
        }
    }

    public void tagAnnotations() {
        List<CtAnnotation<?>> annotations = getElement().getAnnotations();
        for (CtAnnotation annotation : annotations) {
            TypeEntity annotationType = getFactory().wrap(annotation.getAnnotationType());
            getLogger().addTriple(this, Ontology.ANNOTATION_PROPERTY, annotationType);
            annotationType.follow();
        }
    }
}
