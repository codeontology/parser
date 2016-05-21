package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.support.FlowBreakerEntity;
import org.codeontology.extraction.support.TargetedLabelTagger;
import spoon.reflect.code.CtContinue;

public class ContinueEntity extends StatementEntity<CtContinue> implements FlowBreakerEntity<CtContinue> {

    public ContinueEntity(CtContinue element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CONTINUE_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagTargetedLabel();
    }

    @Override
    public void tagTargetedLabel() {
        new TargetedLabelTagger(this).tagTargetedLabel();
    }

    @Override
    public String getTargetedLabel() {
        return getElement().getTargetLabel();
    }
}
