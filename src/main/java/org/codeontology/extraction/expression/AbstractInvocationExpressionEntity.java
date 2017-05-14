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

package org.codeontology.extraction.expression;

import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtExecutableReference;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInvocationExpressionEntity<T extends CtAbstractInvocation<?> & CtExpression<?>>
        extends ExpressionEntity<T> {
    public AbstractInvocationExpressionEntity(T expression) {
        super(expression);
    }

    @Override
    public void extract() {
        super.extract();
        tagExecutable();
        tagArguments();
    }

    public void tagExecutable() {
        ExecutableEntity<?> executable = getExecutable();
        if (executable != null) {
            getLogger().addTriple(this, Ontology.INVOKES_PROPERTY, executable);
            executable.follow();
        }
    }

    public void tagArguments() {
        List<ActualArgumentEntity> arguments = getArguments();
        for (ActualArgumentEntity argument : arguments) {
            getLogger().addTriple(this, Ontology.ARGUMENT_PROPERTY, argument);
            argument.extract();
        }
    }

    public ExecutableEntity<?> getExecutable() {
        CtExecutableReference<?> reference = getElement().getExecutable();
        if (reference != null) {
            ExecutableEntity<?> executable = getFactory().wrap(reference);
            TypeEntity<?> declaringType = getFactory().wrap(reference.getDeclaringType());
            executable.setParent(declaringType);
            return executable;
        }

        return null;
    }

    public List<ActualArgumentEntity> getArguments() {
        List<CtExpression<?>> expressions = getElement().getArguments();
        List<ActualArgumentEntity> arguments = new ArrayList<>();

        if (expressions == null) {
            return arguments;
        }

        int size = expressions.size();

        for (int i = 0; i < size; i++) {
            ExpressionEntity<?> expression = getFactory().wrap(expressions.get(i));
            ActualArgumentEntity argument = new ActualArgumentEntity(expression);
            expression.setParent(argument);
            argument.setParent(this);
            argument.setPosition(i);
            arguments.add(argument);
        }

        return arguments;
    }
}