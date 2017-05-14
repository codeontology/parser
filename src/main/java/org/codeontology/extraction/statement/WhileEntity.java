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
import org.codeontology.extraction.support.ConditionHolderEntity;
import org.codeontology.extraction.support.ConditionTagger;
import spoon.reflect.code.CtWhile;

public class WhileEntity extends LoopEntity<CtWhile> implements ConditionHolderEntity<CtWhile> {

    public WhileEntity(CtWhile element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.WHILE_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagCondition();
    }

    @Override
    public ExpressionEntity<?> getCondition() {
        ExpressionEntity<?> condition = getFactory().wrap(getElement().getLoopingExpression());
        condition.setParent(this);
        return condition;
    }

    @Override
    public void tagCondition() {
        new ConditionTagger(this).tagCondition();
    }
}