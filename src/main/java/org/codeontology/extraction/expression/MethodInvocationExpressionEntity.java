package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class MethodInvocationExpressionEntity extends ExpressionEntity<CtInvocation<?>>
        implements ExpressionHolderEntity<CtInvocation<?>> {

    public MethodInvocationExpressionEntity(CtInvocation<?> expression) {
        super(expression);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_INVOCATION_EXPRESSION_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagTarget();
        tagArguments();
        tagMethod();
    }

    public void tagMethod() {
        ExecutableEntity<?> method = getMethod();
        if (method != null) {
            getLogger().addTriple(this, Ontology.INVOKES_PROPERTY, method);
            method.follow();
        }
    }

    public void tagArguments() {
        List<ActualArgumentEntity> arguments = getArguments();
        for (ActualArgumentEntity argument : arguments) {
            getLogger().addTriple(this, Ontology.ARGUMENT_PROPERTY, argument);
            argument.extract();
        }
    }

    public void tagTarget() {
        CtExpression<?> target = getElement().getTarget();

        if (!(target instanceof CtTypeAccess<?>)) {
            tagExpression();
            return;
        }

        CtTypeReference<?> reference = ((CtTypeAccess<?>) target).getType();
        TypeEntity<?> type = getFactory().wrap(reference);
        if (type != null) {
            getLogger().addTriple(this, Ontology.TARGET_PROPERTY, type);
            type.follow();
        }
    }

    public ExecutableEntity<?> getMethod() {
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

    @Override
    public ExpressionEntity<?> getExpression() {
        CtExpression<?> target = getElement().getTarget();
        if (target != null) {
            ExpressionEntity<?> expression = getFactory().wrap(target);
            expression.setParent(this);
            return expression;
        }
        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression(Ontology.TARGET_PROPERTY);
    }
}
