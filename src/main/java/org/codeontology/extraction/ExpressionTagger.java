package org.codeontology.extraction;

import org.codeontology.Ontology;

public class ExpressionTagger {

    ExpressionHolderEntity<?> entity;

    public ExpressionTagger(ExpressionHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagExpression() {
        ExpressionEntity expression = entity.getExpression();
        RDFLogger.getInstance().addTriple(entity, Ontology.EXPRESSION_PROPERTY, expression);
        expression.extract();
    }
}
