package org.codeontology.extraction;

public interface ExpressionHolderEntity<E> extends Entity<E> {

    ExpressionEntity getExpression();

    void tagExpression();
}
