package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

public class IfThenElseEntity extends StatementEntity<CtIf> {

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

    public void tagCondition() {
        CtExpression<Boolean> condition = getElement().getCondition();
        ExpressionEntity conditionEntity = getFactory().wrap(condition);
        conditionEntity.setParent(this);
        getLogger().addTriple(this, Ontology.CONDITION_PROPERTY, conditionEntity);
        conditionEntity.extract();
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
