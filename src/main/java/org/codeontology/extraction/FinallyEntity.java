package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

import java.util.List;

public class FinallyEntity extends CodeElementEntity<CtBlock<?>> {

    private static final String TAG = "finally";

    public FinallyEntity(CtBlock<?> block) {
        super(block);
    }

    @Override
    protected String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG;
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {
        tagType();
        tagStatements();
        tagSourceCode();
        tagLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    public void tagStatements() {
        List<CtStatement> statements = getElement().getStatements();
        int size = statements.size();
        for (int i = 0; i < size; i++) {
            StatementEntity<?> statement = getFactory().wrap(statements.get(i));
            statement.setPosition(i);
            statement.setParent(this);
            getLogger().addTriple(this, Ontology.STATEMENT_PROPERTY, statement);
            statement.extract();
        }
    }
}
