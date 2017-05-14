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

package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;

public class AssertEntity extends StatementEntity<CtAssert<?>> implements ExpressionHolderEntity<CtAssert<?>> {

    public AssertEntity(CtAssert element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ASSERT_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
        tagAssertExpression();
    }

    public ExpressionEntity<?> getAssertExpression() {
        CtExpression<Boolean> expression = getElement().getAssertExpression();
        ExpressionEntity<?> entity = getFactory().wrap(expression);
        entity.setParent(this);
        return entity;
    }

    public void tagAssertExpression() {
        ExpressionEntity<?> assertExpression = getAssertExpression();
        getLogger().addTriple(this, Ontology.ASSERT_EXPRESSION_PROPERTY, assertExpression);
        assertExpression.extract();
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        CtExpression<?> expression = getElement().getExpression();
        if (expression != null) {
            ExpressionEntity<?> entity = getFactory().wrap(expression);
            entity.setParent(this);
            return entity;
        }

        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}