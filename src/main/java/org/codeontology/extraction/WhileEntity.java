package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;

public class WhileEntity extends StatementEntity<CtWhile> implements ConditionHolderEntity<CtWhile>, BodyHolderEntity<CtWhile> {

    public WhileEntity(CtWhile element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.WHILE_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagCondition();
        tagBody();
    }

    @Override
    public StatementEntity<?> getBody() {
        CtStatement statement = getElement().getBody();
        if (statement != null) {
            StatementEntity<?> body = getFactory().wrap(statement);
            body.setParent(this);
            return body;
        }

        return null;
    }

    @Override
    public void tagBody() {
        new BodyTagger(this).tagBody();
    }

    @Override
    public ExpressionEntity getCondition() {
        ExpressionEntity condition = getFactory().wrap(getElement().getLoopingExpression());
        condition.setParent(this);
        return condition;
    }

    @Override
    public void tagCondition() {
        new ConditionTagger(this).tagCondition();
    }
}
