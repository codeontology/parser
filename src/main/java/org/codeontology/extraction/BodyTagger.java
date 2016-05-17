package org.codeontology.extraction;

import org.codeontology.Ontology;

public class BodyTagger {

    BodyHolderEntity<?> entity;

    public BodyTagger(BodyHolderEntity<?> entity) {
        this.entity = entity;
    }

    public void tagBody() {
        StatementEntity<?> body = entity.getBody();
        if (body != null) {
            RDFLogger.getInstance().addTriple(entity, Ontology.BODY_PROPERTY, body);
            body.extract();
        }
    }
}
