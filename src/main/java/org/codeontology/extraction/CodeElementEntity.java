/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

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
        return buildRelativeURI("");
    }

    protected String buildRelativeURI(String tag) {
        SourcePosition position = getElement().getPosition();
        StringBuilder builder = new StringBuilder();
        if (tag == null) {
            tag = "";
        }

        tag = tag.trim();

        if (position == null) {
            builder.append(getParent().getRelativeURI());
            if (!tag.equals("")) {
                builder.append(SEPARATOR).append(tag);
            }
            builder.append(SEPARATOR).append("-1");
            return builder.toString();
        }

        CtType<?> mainType = position.getCompilationUnit().getMainType();
        TypeEntity<?> mainTypeEntity = getFactory().wrap(mainType);
        builder.append(mainTypeEntity.getRelativeURI());
        if (!tag.equals("")) {
            builder.append(SEPARATOR).append(tag);
        }
        builder.append(SEPARATOR)
                .append(position.getLine())
                .append(SEPARATOR)
                .append(position.getColumn())
                .append(SEPARATOR)
                .append(position.getEndColumn());

        return builder.toString();
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