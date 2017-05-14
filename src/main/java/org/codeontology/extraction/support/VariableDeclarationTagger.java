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

package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.EntityFactory;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.expression.ExpressionEntity;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtVariable;

public class VariableDeclarationTagger {
    private final VariableDeclarationEntity<?> declaration;

    public VariableDeclarationTagger(VariableDeclarationEntity<?> declaration) {
        this.declaration = declaration;
    }

    public void tagVariable() {
        Entity<?> variable = declaration.getVariable();
        if (variable != null) {
            RDFLogger.getInstance().addTriple(variable, Ontology.DECLARATION_PROPERTY, declaration);
            variable.extract();
        }
    }

    public void tagInitializer() {
        ExpressionEntity<?> expression = declaration.getInitializer();
        if (expression != null) {
            RDFLogger.getInstance().addTriple(declaration, Ontology.INITIALIZER_PROPERTY, expression);
            expression.extract();
        }
    }

    public static ExpressionEntity<?> initializerOf(VariableDeclarationEntity<? extends CtVariable<?>> declaration) {
        CtExpression<?> defaultExpression = declaration.getElement().getDefaultExpression();
        if (defaultExpression != null) {
            ExpressionEntity<?> initializer = EntityFactory.getInstance().wrap(defaultExpression);
            initializer.setParent(declaration);
            return initializer;
        }

        return null;
    }

    public static Entity<?> declaredVariableOf(VariableDeclarationEntity<? extends CtVariable<?>> declaration) {
        Entity<?> declaredVariable = EntityFactory.getInstance().wrap(declaration.getElement());
        if (declaredVariable != null) {
            declaredVariable.setParent(declaration);
            return declaredVariable;
        }
        return null;
    }
}