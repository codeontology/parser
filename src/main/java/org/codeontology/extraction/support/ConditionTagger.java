package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.expression.ExpressionEntity;

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
