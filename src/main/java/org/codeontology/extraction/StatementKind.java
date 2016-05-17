package org.codeontology.extraction;

import spoon.reflect.code.*;

public enum StatementKind {
    BLOCK,
    IF_THEN_ELSE,
    WHILE,
    DO,
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
        }

        return STATEMENT;
    }
}
