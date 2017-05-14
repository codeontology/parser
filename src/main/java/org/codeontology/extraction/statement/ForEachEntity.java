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
import org.codeontology.extraction.declaration.LocalVariableEntity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtForEach;

public class ForEachEntity extends LoopEntity<CtForEach> implements ExpressionHolderEntity<CtForEach> {

    public ForEachEntity(CtForEach element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FOR_EACH_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
        tagVariable();
    }

    public ExpressionEntity<?> getExpression() {
        ExpressionEntity<?> expression = getFactory().wrap(getElement().getExpression());
        expression.setParent(this);
        return expression;
    }

    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }

    private LocalVariableEntity getVariable() {
        LocalVariableEntity variable = getFactory().wrap(getElement().getVariable());
        variable.setParent(this);
        return variable;
    }

    public void tagVariable() {
        LocalVariableEntity variable = getVariable();
        getLogger().addTriple(this, Ontology.VARIABLE_PROPERTY, variable);
        variable.extract();
    }
}