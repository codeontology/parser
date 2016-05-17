package org.codeontology.extraction;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;

public enum StatementKind {
    BLOCK,
    IF_THEN_ELSE,
    WHILE,
    STATEMENT;

    public static StatementKind getKindOf(CtStatement statement) {
        if (statement instanceof CtBlock) {
            return BLOCK;
        } else if (statement instanceof CtIf){
            return IF_THEN_ELSE;
        } else if (statement instanceof CtWhile) {
            return WHILE;
        }

        return STATEMENT;
    }
}
