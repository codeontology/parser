package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtCase;

public class DefaultLabelEntity extends SwitchLabelEntity {
    public DefaultLabelEntity(CtCase<?> label) {
        super(label);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.DEFAULT_ENTITY;
    }
}
