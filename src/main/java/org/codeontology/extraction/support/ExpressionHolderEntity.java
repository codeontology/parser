package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;

public interface ExpressionHolderEntity<E> extends Entity<E> {

    ExpressionEntity<?> getExpression();

    void tagExpression();
}
