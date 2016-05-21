package org.codeontology.extraction.support;


import org.codeontology.extraction.Entity;
import org.codeontology.extraction.statement.StatementEntity;
import spoon.reflect.declaration.CtElement;

public interface BodyHolderEntity<E extends CtElement> extends Entity<E> {

    StatementEntity<?> getBody();

    void tagBody();

}
