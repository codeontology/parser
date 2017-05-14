/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

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