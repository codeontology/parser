package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;

public class LineTagger {
    CodeElementEntity<?> entity;

    public LineTagger(CodeElementEntity<?> entity) {
        this.entity = entity;
    }

    public void tagLine() {
        int line = entity.getElement().getPosition().getLine();
        Literal lineLiteral = entity.getModel().createTypedLiteral(line);
        RDFLogger.getInstance().addTriple(entity, Ontology.LINE_PROPERTY, lineLiteral);
    }
}
