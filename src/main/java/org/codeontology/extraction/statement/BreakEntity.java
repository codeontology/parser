package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.support.FlowBreakerEntity;
import org.codeontology.extraction.support.TargetedLabelTagger;
import spoon.reflect.code.CtBreak;

public class BreakEntity extends StatementEntity<CtBreak> implements FlowBreakerEntity<CtBreak> {

    public BreakEntity(CtBreak element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.BREAK_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagTargetedLabel();
    }

    public void tagTargetedLabel() {
        new TargetedLabelTagger(this).tagTargetedLabel();
    }

    @Override
    public String getTargetedLabel() {
        return getElement().getTargetLabel();
    }
}
