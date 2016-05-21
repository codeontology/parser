package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtThrow;

public class ThrowEntity extends StatementEntity<CtThrow> implements ExpressionHolderEntity<CtThrow> {
    public ThrowEntity(CtThrow statement) {
        super(statement);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.THROW_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
    }

    @Override
    public ExpressionEntity getExpression() {
        CtExpression<?> thrownExpression = getElement().getThrownExpression();
        ExpressionEntity expression = getFactory().wrap(thrownExpression);
        expression.setParent(this);
        return expression;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression(Ontology.THROWN_EXPRESSION_PROPERTY);
    }
}
