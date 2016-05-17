package org.codeontology.extraction;

import spoon.reflect.declaration.CtElement;

public interface ConditionHolderEntity<E extends CtElement> extends Entity<E> {

    ExpressionEntity getCondition();

    void tagCondition();

}
