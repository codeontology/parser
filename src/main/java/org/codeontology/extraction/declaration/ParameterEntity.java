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

package org.codeontology.extraction.declaration;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.docparser.DocCommentParser;
import org.codeontology.docparser.ParamTag;
import org.codeontology.docparser.Tag;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.support.JavaTypeTagger;
import org.codeontology.extraction.support.TypedElementEntity;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class ParameterEntity extends NamedElementEntity<CtParameter<?>> implements TypedElementEntity<CtParameter<?>> {

    private int position;
    private ExecutableEntity<? extends CtExecutable> parent;
    private boolean parameterAvailable = true;
    private static final String TAG = "parameter";

    public ParameterEntity(CtParameter<?> parameter) {
        super(parameter);
        parameterAvailable = true;
    }

    public ParameterEntity(CtTypeReference<?> reference) {
        super(reference);
        parameterAvailable = false;
        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagPosition();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagName();
            tagLabel();
            tagComment();
        }
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
    }

    public void tagPosition() {
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setParent(ExecutableEntity<?> parent) {
        this.parent = parent;
    }

    public ExecutableEntity<?> getParent() {
        return this.parent;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETER_ENTITY;
    }

    @Override
    public TypeEntity<?> getJavaType() {
        TypeEntity<?> type;
        if (isDeclarationAvailable()) {
            type = getFactory().wrap(getElement().getType());
        } else {
            type = getFactory().wrap((CtTypeReference<?>) getReference());
        }
        type.setParent(parent);
        return type;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }

    @Override
    public boolean isDeclarationAvailable() {
        return parameterAvailable;
    }

    @Override
    public void tagComment() {
        if (!parent.isDeclarationAvailable()) {
            return;
        }
        String methodComment = parent.getElement().getDocComment();
        if (methodComment == null) {
            return;
        }
        DocCommentParser parser = new DocCommentParser(methodComment);
        List<Tag> tags = parser.getParamTags();

        for (Tag tag : tags) {
            ParamTag paramTag = (ParamTag) tag;
            if (paramTag.getParameterName().equals(getElement().getSimpleName())) {
                Literal comment = getModel().createLiteral(paramTag.getParameterComment());
                getLogger().addTriple(this, Ontology.COMMENT_PROPERTY, comment);
                break;
            }
        }
    }
}