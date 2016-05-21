package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;

public class TargetedLabelTagger {

    private FlowBreakerEntity<?> entity;

    public TargetedLabelTagger(FlowBreakerEntity<?> entity) {
        this.entity = entity;
    }

    public void tagTargetedLabel() {
        String labelString = entity.getTargetedLabel();
        if (labelString != null) {
            Literal label = entity.getModel().createTypedLiteral(labelString);
            RDFLogger.getInstance().addTriple(entity, Ontology.TARGETED_LABEL_PROPERTY, label);
        }
    }
}
