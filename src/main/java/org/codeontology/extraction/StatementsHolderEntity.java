package org.codeontology.extraction;

import java.util.List;

public interface StatementsHolderEntity<E> extends Entity<E> {

    List<StatementEntity<?>> getStatements();

    void tagStatements();

}
