package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;
import spoon.reflect.declaration.CtElement;

public interface ConditionHolderEntity<E extends CtElement> extends Entity<E> {

    ExpressionEntity<?> getCondition();

    void tagCondition();

}
