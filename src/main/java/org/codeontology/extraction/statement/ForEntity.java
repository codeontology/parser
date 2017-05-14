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
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ConditionHolderEntity;
import org.codeontology.extraction.support.ConditionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;

import java.util.List;

public class ForEntity extends LoopEntity<CtFor> implements ConditionHolderEntity<CtFor> {

    public ForEntity(CtFor element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FOR_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagForInit();
        tagCondition();
        tagForUpdate();
    }

    @Override
    public ExpressionEntity<?> getCondition() {
        CtExpression<?> expression = getElement().getExpression();
        if (expression != null) {
            ExpressionEntity<?> condition = getFactory().wrap(expression);
            condition.setParent(this);
            return condition;
        }

        return null;
    }

    @Override
    public void tagCondition() {
        new ConditionTagger(this).tagCondition();
    }

    private StatementExpressionListEntity getForInit() {
        StatementExpressionListEntity forInit =  getFactory().wrap(getElement().getForInit());
        forInit.setParent(this);
        forInit.setPosition(0);
        return forInit;
    }

    public void tagForInit() {
        List<Entity<?>> forInit = getForInit().getElement();
        for (Entity<?> init : forInit) {
            getLogger().addTriple(this, Ontology.FOR_INIT_PROPERTY, init);
            init.extract();
        }
    }

    private StatementExpressionListEntity getForUpdate() {
        StatementExpressionListEntity forUpdate =  getFactory().wrap(getElement().getForUpdate());
        forUpdate.setPosition(2);
        forUpdate.setParent(this);
        return forUpdate;
    }

    public void tagForUpdate() {
        List<Entity<?>> forUpdate = getForUpdate().getElement();
        for (Entity<?> update : forUpdate) {
            getLogger().addTriple(this, Ontology.FOR_UPDATE_PROPERTY, update);
            update.extract();
        }
    }
}