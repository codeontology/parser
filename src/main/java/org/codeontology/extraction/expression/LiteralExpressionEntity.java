package org.codeontology.extraction.expression;

import spoon.reflect.code.CtLiteral;

public class LiteralExpressionEntity<T> extends ExpressionEntity<CtLiteral<T>> {

    public LiteralExpressionEntity(CtLiteral<T> expression) {
        super(expression);
    }
}