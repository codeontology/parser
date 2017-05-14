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

package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import org.codeontology.extraction.support.JavaTypeTagger;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.TypedElementEntity;
import spoon.reflect.code.CtExpression;

public class ExpressionEntity<E extends CtExpression<?>> extends CodeElementEntity<E>
        implements TypedElementEntity<E> {

    public ExpressionEntity(E expression) {
        super(expression);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.EXPRESSION_ENTITY;
    }

    @Override
    public String buildRelativeURI() {
        return super.buildRelativeURI("expression");
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagSourceCode();
        tagLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    @Override
    public TypeEntity<?> getJavaType() {
        TypeEntity<?> type = getFactory().wrap(getElement().getType());
        if (type != null) {
            type.setParent(getParent(GenericDeclarationEntity.class));
            return type;
        }

        return null;
    }

    @Override
    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }
}