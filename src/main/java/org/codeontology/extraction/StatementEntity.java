package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtStatement;

public class StatementEntity<E extends CtStatement> extends AbstractEntity<E> {
    private static final String TAG = "statement";
    private int position;

    public StatementEntity(E element) {
        super(element);
    }

    @Override
    public String buildRelativeURI() {
        if (!(getParent() instanceof StatementEntity)) {
            return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
        } else {
            return getParent().getRelativeURI() + SEPARATOR + position;
        }
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
        tagParent();
        tagPosition();
        tagLine();
        tagSourceCode();
    }

    private void tagParent() {
        getLogger().addTriple(getParent(), Ontology.STATEMENT_PROPERTY, this);
    }

    public int getLine() {
        return getElement().getPosition().getLine();
    }

    public void tagLine() {
        Literal line = getModel().createTypedLiteral(getLine());
        RDFLogger.getInstance().addTriple(this, Ontology.LINE_PROPERTY, line);
    }

    public void tagPosition() {
        Literal position = getModel().createTypedLiteral(getPosition());
        RDFLogger.getInstance().addTriple(this, Ontology.POSITION_PROPERTY, position);
    }
}
