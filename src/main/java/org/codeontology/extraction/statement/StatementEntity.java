package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.support.LineTagger;
import spoon.reflect.code.CtStatement;

public class StatementEntity<E extends CtStatement> extends CodeElementEntity<E> {
    private int position;

    public StatementEntity(E element) {
        super(element);
    }

    @Override
    public String buildRelativeURI() {
        return super.buildRelativeURI("statement");
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.STATEMENT_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagPosition();
        tagLine();
        tagSourceCode();
        tagLabel();
    }

    public void tagLabel() {
        String labelString = getElement().getLabel();
        if (labelString != null) {
            Literal label = getModel().createTypedLiteral(labelString);
            getLogger().addTriple(this, Ontology.WOC_LABEL_PROPERTY, label);
        }
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    public void tagPosition() {
        Literal position = getModel().createTypedLiteral(getPosition());
        RDFLogger.getInstance().addTriple(this, Ontology.POSITION_PROPERTY, position);
    }
}
