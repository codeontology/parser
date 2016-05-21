package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ConditionHolderEntity;
import org.codeontology.extraction.support.ConditionTagger;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

public class IfThenElseEntity extends StatementEntity<CtIf> implements ConditionHolderEntity<CtIf> {

    public IfThenElseEntity(CtIf element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.IF_THEN_ELSE_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagCondition();
        tagThenStatement();
        tagElseStatement();
    }

    @Override
    public ExpressionEntity getCondition() {
        ExpressionEntity condition = getFactory().wrap(getElement().getCondition());
        condition.setParent(this);
        return condition;
    }

    @Override
    public void tagCondition() {
        new ConditionTagger(this).tagCondition();
    }

    public void tagThenStatement() {
        CtStatement thenStatement = getElement().getThenStatement();
        StatementEntity<?> statement = getFactory().wrap(thenStatement);
        statement.setParent(this);
        statement.setPosition(0);
        getLogger().addTriple(this, Ontology.THEN_PROPERTY, statement);
        statement.extract();
    }

    public void tagElseStatement() {
        CtStatement elseStatement = getElement().getElseStatement();
        if (elseStatement != null) {
            StatementEntity<?> statement = getFactory().wrap(elseStatement);
            statement.setParent(this);
            statement.setPosition(1);
            getLogger().addTriple(this, Ontology.ELSE_PROPERTY, statement);
            statement.extract();
        }
    }

}
