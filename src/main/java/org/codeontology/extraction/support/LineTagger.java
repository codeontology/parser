package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.RDFLogger;

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

    public void tagEndLine() {
        int endLine = entity.getElement().getPosition().getEndLine();
        Literal literal = entity.getModel().createTypedLiteral(endLine);
        RDFLogger.getInstance().addTriple(entity, Ontology.END_LINE_PROPERTY, literal);
    }
}
