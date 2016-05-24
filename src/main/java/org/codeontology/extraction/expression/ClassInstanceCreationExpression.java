package org.codeontology.extraction.expression;

import spoon.reflect.code.CtConstructorCall;

public class ClassInstanceCreationExpression extends AbstractInvocationExpressionEntity<CtConstructorCall<?>> {
    public ClassInstanceCreationExpression(CtConstructorCall<?> expression) {
        super(expression);
        tagArguments();
        tagExecutable();
    }
}
