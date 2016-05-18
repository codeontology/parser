package org.codeontology.extraction;

import spoon.reflect.code.*;

public enum StatementKind {
    BLOCK,
    IF_THEN_ELSE,
    WHILE,
    DO,
    FOR,
    FOREACH,
    TRY,
    STATEMENT;

    public static StatementKind getKindOf(CtStatement statement) {
        if (statement instanceof CtBlock) {
            return BLOCK;
        } else if (statement instanceof CtIf){
            return IF_THEN_ELSE;
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
        }

        return STATEMENT;
    }
}
