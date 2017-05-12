package org.codeontology.extraction.statement;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;

public enum StatementKind {
    BLOCK,
    IF_THEN_ELSE,
    SWITCH,
    WHILE,
    DO,
    FOR,
    FOREACH,
    TRY,
    RETURN,
    THROW,
    BREAK,
    CONTINUE,
    ASSERT,
    SYNCHRONIZED,
    EXPRESSION_STATEMENT,
    LOCAL_VARIABLE_DECLARATION,
    CLASS_DECLARATION,
    STATEMENT;

    public static StatementKind getKindOf(CtStatement statement) {
        if (statement instanceof CtBlock) {
            return BLOCK;
        } else if (statement instanceof CtIf) {
            return IF_THEN_ELSE;
        } else if (statement instanceof CtSwitch) {
            return SWITCH;
        } else if (statement instanceof CtWhile) {
            return WHILE;
        } else if (statement instanceof CtDo) {
            return DO;
        } else if (statement instanceof CtFor) {
            return FOR;
        } else if (statement instanceof CtForEach) {
            return FOREACH;
        } else if (statement instanceof CtTry) {
            return TRY;
        } else if (statement instanceof CtReturn<?>) {
            return RETURN;
        } else if (statement instanceof CtThrow) {
            return THROW;
        } else if (statement instanceof CtBreak) {
            return BREAK;
        } else if (statement instanceof CtContinue) {
            return CONTINUE;
        } else if (statement instanceof CtAssert) {
            return ASSERT;
        } else if (statement instanceof CtSynchronized) {
            return SYNCHRONIZED;
        } else if (statement instanceof CtLocalVariable<?>) {
            return LOCAL_VARIABLE_DECLARATION;
        } else if (statement instanceof CtClass<?>) {
            return CLASS_DECLARATION;
        } else if (statement instanceof CtExpression<?>) {
            return EXPRESSION_STATEMENT;
        }

        return STATEMENT;
    }
}
