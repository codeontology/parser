package org.codeontology.extraction.statement;

import spoon.reflect.code.*;

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
        }

        return STATEMENT;
    }
}
