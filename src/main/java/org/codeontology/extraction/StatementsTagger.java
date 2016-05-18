package org.codeontology.extraction;

import org.codeontology.Ontology;

import java.util.List;

public class StatementsTagger {

    private final StatementsHolderEntity<?> entity;

    public StatementsTagger(StatementsHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagStatements() {
        List<StatementEntity<?>> statements = entity.getStatements();
        for (StatementEntity<?> statement : statements) {
            RDFLogger.getInstance().addTriple(entity, Ontology.STATEMENT_PROPERTY, statement);
            statement.extract();
        }
    }
}
