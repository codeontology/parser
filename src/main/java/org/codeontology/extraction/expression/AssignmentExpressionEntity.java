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
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtAssignment;

public class AssignmentExpressionEntity extends ExpressionEntity<CtAssignment<?, ?>>
        implements ExpressionHolderEntity<CtAssignment<?, ?>> {

    public AssignmentExpressionEntity(CtAssignment<?, ?> expression) {
        super(expression);
    }

    @Override
    public void extract() {
        super.extract();
        tagLeftHandSideExpression();
        tagExpression();
    }

    public void tagLeftHandSideExpression() {
        ExpressionEntity<?> expression = getLeftHandSideExpression();
        getLogger().addTriple(this, Ontology.LEFT_HAND_SIDE_PROPERTY, expression);
        expression.extract();
    }

    public ExpressionEntity<?> getLeftHandSideExpression() {
        ExpressionEntity<?> leftHandExpression = getFactory().wrap(getElement().getAssigned());
        leftHandExpression.setParent(this);
        return leftHandExpression;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ASSIGNMENT_EXPRESSION_ENTITY;
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        ExpressionEntity<?> expression = getFactory().wrap(getElement().getAssignment());
        expression.setParent(this);
        return expression;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}