package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.statement.StatementEntity;

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
}
