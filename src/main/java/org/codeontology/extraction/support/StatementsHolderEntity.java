package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.statement.StatementEntity;

import java.util.List;

public interface StatementsHolderEntity<E> extends Entity<E> {

    List<StatementEntity<?>> getStatements();

    void tagStatements();

}
