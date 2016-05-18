package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtFor;

public class ForEntity extends LoopEntity<CtFor> implements ConditionHolderEntity<CtFor> {

    public ForEntity(CtFor element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FOR_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagForInit();
        tagCondition();
        tagForUpdate();
    }

    @Override
    public ExpressionEntity getCondition() {
        ExpressionEntity condition = getFactory().wrap(getElement().getExpression());
        condition.setParent(this);
        return condition;
    }

    @Override
    public void tagCondition() {
        new ConditionTagger(this).tagCondition();
    }

    private StatementExpressionListEntity getForInit() {
        StatementExpressionListEntity forInit =  getFactory().wrap(getElement().getForInit());
        forInit.setParent(this);
        forInit.setPosition(0);
        return forInit;
    }

    public void tagForInit() {
        StatementExpressionListEntity forInit = getForInit();
        getLogger().addTriple(this, Ontology.FOR_INIT_PROPERTY, forInit);
        forInit.extract();
    }

    private StatementExpressionListEntity getForUpdate() {
        StatementExpressionListEntity forUpdate =  getFactory().wrap(getElement().getForUpdate());
        forUpdate.setPosition(2);
        forUpdate.setParent(this);
        return forUpdate;
    }

    public void tagForUpdate() {
        StatementExpressionListEntity forUpdate = getForUpdate();
        getLogger().addTriple(this, Ontology.FOR_UPDATE_PROPERTY, forUpdate);
        forUpdate.extract();
    }
}
