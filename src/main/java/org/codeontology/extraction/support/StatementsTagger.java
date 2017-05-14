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

package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.EntityFactory;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.statement.StatementEntity;
import spoon.reflect.code.CtStatement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatementsTagger {

    private final StatementsHolderEntity<?> entity;

    public StatementsTagger(StatementsHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagStatements() {
        List<StatementEntity<?>> statements = entity.getStatements();
        Iterator<StatementEntity<?>> iterator = statements.listIterator();

        if (iterator.hasNext()) {
            StatementEntity<?> previous = iterator.next();
            tagStatement(previous);
            while (iterator.hasNext()) {
                StatementEntity<?> current = iterator.next();
                RDFLogger.getInstance().addTriple(previous, Ontology.NEXT_PROPERTY, current);
                previous = current;
                tagStatement(current);
            }
        }
    }

    private void tagStatement(StatementEntity<?> statement) {
        RDFLogger.getInstance().addTriple(entity, Ontology.STATEMENT_PROPERTY, statement);
        statement.extract();
    }

    public List<StatementEntity<?>> asEntities(List<CtStatement> statements) {
        List<StatementEntity<?>> result = new ArrayList<>();

        if (statements == null) {
            return result;
        }

        int size = statements.size();

        for (int i = 0; i < size; i++) {
            StatementEntity<?> statement = EntityFactory.getInstance().wrap(statements.get(i));
            statement.setPosition(i);
            statement.setParent(entity);
            result.add(statement);
        }

        return result;
    }

}