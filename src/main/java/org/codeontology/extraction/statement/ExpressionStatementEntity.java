package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;

public class ExpressionStatementEntity extends StatementEntity<CtStatement> implements ExpressionHolderEntity<CtStatement> {

    public ExpressionStatementEntity(CtStatement element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.EXPRESSION_STATEMENT_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
    }

    @Override
    public ExpressionEntity getExpression() {
        if (getElement() instanceof CtExpression<?>) {
            ExpressionEntity expression = getFactory().wrap((CtExpression<?>) getElement());
            expression.setParent(this);
            return expression;
        }

        return null;
    }


    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}
