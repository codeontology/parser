package org.codeontology.extraction;

import org.codeontology.Ontology;

public class ConditionTagger {

    ConditionHolderEntity entity;

    public ConditionTagger(ConditionHolderEntity entity) {
        this.entity = entity;
    }

    public void tagCondition() {
        ExpressionEntity condition = entity.getCondition();
        RDFLogger.getInstance().addTriple(entity, Ontology.CONDITION_PROPERTY, condition);
        condition.extract();
    }
}
