package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtAssignment;

public class AssignmentExpressionEntity extends ExpressionEntity<CtAssignment<?, ?>>
        implements ExpressionHolderEntity<CtAssignment<?, ?>> {

    public AssignmentExpressionEntity(CtAssignment<?, ?> expression) {
        super(expression);
    }

    @Override
    public void extract() {
        super.extract();
        tagLeftHandSideExpression();
        tagExpression();
    }

    public void tagLeftHandSideExpression() {
        ExpressionEntity<?> expression = getLeftHandSideExpression();
        getLogger().addTriple(this, Ontology.LEFT_HAND_SIDE_PROPERTY, expression);
        expression.extract();
    }

    public ExpressionEntity<?> getLeftHandSideExpression() {
        ExpressionEntity<?> leftHandExpression = getFactory().wrap(getElement().getAssigned());
        leftHandExpression.setParent(this);
        return leftHandExpression;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ASSIGNMENT_EXPRESSION_ENTITY;
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        ExpressionEntity<?> expression = getFactory().wrap(getElement().getAssignment());
        expression.setParent(this);
        return expression;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}
