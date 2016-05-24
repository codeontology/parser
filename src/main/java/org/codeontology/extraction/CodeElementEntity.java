package org.codeontology.extraction;

import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.TypeEntity;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

import java.util.List;

public abstract class CodeElementEntity<E extends CtElement> extends AbstractEntity<E> {

    protected CodeElementEntity() {

    }

    protected CodeElementEntity(E element) {
        super(element);
    }

    @Override
    public String buildRelativeURI() {
        SourcePosition position = getElement().getPosition();

        if (position == null) {
            return getParent().getRelativeURI() + SEPARATOR + "-1";
        }

        CtType<?> mainType = position.getCompilationUnit().getMainType();
        TypeEntity<?> mainTypeEntity = getFactory().wrap(mainType);
        return mainTypeEntity.getRelativeURI() + SEPARATOR + position.getLine() + SEPARATOR + position.getColumn();
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
