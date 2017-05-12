package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtTypeReference;

public class MethodInvocationExpressionEntity extends AbstractInvocationExpressionEntity<CtInvocation<?>>
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
        tagExecutable();
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
