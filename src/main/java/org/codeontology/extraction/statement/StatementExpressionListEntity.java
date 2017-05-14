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
import org.codeontology.extraction.AbstractEntity;
import org.codeontology.extraction.Entity;

import java.util.List;

public class StatementExpressionListEntity extends AbstractEntity<List<Entity<?>>> {

    private int position;

    private static final String TAG = "statement-expression-list";

    public StatementExpressionListEntity(List<Entity<?>> list) {
        super(list);
        for (Entity<?> current : list) {
            current.setParent(this);
        }
    }

    @Override
    protected String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.STATEMENT_EXPRESSION_LIST_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
    }

    @Override
    public String getSourceCode() {
        List<Entity<?>> list = getElement();
        int size = list.size();
        StringBuilder builder = new StringBuilder();

        if (size > 0) {
            builder.append(list.get(0).getElement());
        }

        for (int i = 1; i < size; i++) {
            builder.append(", ");
            builder.append(list.get(i).getElement());
        }

        return builder.toString();
    }

    public void setPosition(int position) {
        this.position = position;
    }
}