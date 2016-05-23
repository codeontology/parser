package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.expression.ExpressionEntity;

public class ExpressionTagger {

    ExpressionHolderEntity<?> entity;

    public ExpressionTagger(ExpressionHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagExpression() {
        tagExpression(Ontology.EXPRESSION_PROPERTY);
    }

    public void tagExpression(Property property) {
        ExpressionEntity<?> expression = entity.getExpression();
        if (expression != null) {
            RDFLogger.getInstance().addTriple(entity, property, expression);
            expression.extract();
        }
    }
}
