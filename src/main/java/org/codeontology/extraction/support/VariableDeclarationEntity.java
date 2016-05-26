package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;

public interface VariableDeclarationEntity<T> extends Entity<T> {

    ExpressionEntity<?> getInitializer();

    void tagInitializer();

    Entity<?> getVariable();

    void tagVariable();
}
