package org.codeontology.extraction;


import spoon.reflect.declaration.CtElement;

public interface BodyHolderEntity<E extends CtElement> extends Entity<E> {

    StatementEntity<?> getBody();

    void tagBody();

}
