package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.expression.ExpressionEntity;

public class ExpressionTagger {

    ExpressionHolderEntity<?> entity;

    public ExpressionTagger(ExpressionHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagExpression() {
        ExpressionEntity expression = entity.getExpression();
        if (expression != null) {
            RDFLogger.getInstance().addTriple(entity, Ontology.EXPRESSION_PROPERTY, expression);
            expression.extract();
        }
    }
}
