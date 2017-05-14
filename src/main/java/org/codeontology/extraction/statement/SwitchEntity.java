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
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSwitch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SwitchEntity extends StatementEntity<CtSwitch<?>> implements ExpressionHolderEntity<CtSwitch<?>> {

    public SwitchEntity(CtSwitch element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.SWITCH_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
        tagSwitchLabels();
    }

    public void tagSwitchLabels() {
        List<SwitchLabelEntity> labels = getSwitchLabels();
        for (SwitchLabelEntity label : labels) {
            getLogger().addTriple(this, Ontology.SWITCH_LABEL_PROPERTY, label);
            label.extract();
        }
    }

    public List<SwitchLabelEntity> getSwitchLabels() {
        List<CtCase<?>> labels = new ArrayList<>(getElement().getCases());
        List<SwitchLabelEntity> result = new ArrayList<>();

        Iterator<CtCase<?>> iterator = labels.iterator();
        if (iterator.hasNext()) {
            SwitchLabelEntity previous = getFactory().wrap(iterator.next());
            previous.setParent(this);
            result.add(previous);

            while (iterator.hasNext()) {
                SwitchLabelEntity current = getFactory().wrap(iterator.next());
                current.setParent(this);
                result.add(current);
                previous.setNext(current);
            }
        }

        return result;
    }


    @Override
    public ExpressionEntity<?> getExpression() {
        CtExpression<?> selector = getElement().getSelector();
        if (selector == null) {
            return null;
        }

        ExpressionEntity<?> expression = getFactory().wrap(selector);
        expression.setParent(this);
        return expression;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}