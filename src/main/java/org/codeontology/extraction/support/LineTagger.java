package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.RDFLogger;
import spoon.reflect.cu.SourcePosition;

public class LineTagger {
    private CodeElementEntity<?> entity;
    private SourcePosition position;

    public LineTagger(CodeElementEntity<?> entity) {
        this.entity = entity;
        position = entity.getElement().getPosition();
    }

    public void tagLine() {
        if (position == null) {
            return;
        }
        Literal line = entity.getModel().createTypedLiteral(position.getLine());
        RDFLogger.getInstance().addTriple(entity, Ontology.LINE_PROPERTY, line);
    }

    public void tagEndLine() {
        if (position == null) {
            return;
        }

        Literal endLine = entity.getModel().createTypedLiteral(position.getEndLine());
        RDFLogger.getInstance().addTriple(entity, Ontology.END_LINE_PROPERTY, endLine);
    }
}
