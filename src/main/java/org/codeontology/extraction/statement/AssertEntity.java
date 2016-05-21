package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;

public class AssertEntity extends StatementEntity<CtAssert<?>> implements ExpressionHolderEntity<CtAssert<?>> {

    public AssertEntity(CtAssert element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ASSERT_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
        tagAssertExpression();
    }

    public ExpressionEntity getAssertExpression() {
        CtExpression<?> expression = getElement().getAssertExpression();
        ExpressionEntity entity = getFactory().wrap(expression);
        entity.setParent(this);
        return entity;
    }

    public void tagAssertExpression() {
        ExpressionEntity assertExpression = getAssertExpression();
        getLogger().addTriple(this, Ontology.ASSERT_EXPRESSION_PROPERTY, assertExpression);
        assertExpression.extract();
    }

    @Override
    public ExpressionEntity getExpression() {
        CtExpression<?> expression = getElement().getExpression();
        if (expression != null) {
            ExpressionEntity entity = getFactory().wrap(expression);
            entity.setParent(this);
            return entity;
        }

        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}
