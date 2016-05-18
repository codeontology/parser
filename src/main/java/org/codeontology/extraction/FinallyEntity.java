package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

import java.util.ArrayList;
import java.util.List;

public class FinallyEntity extends CodeElementEntity<CtBlock<?>> implements StatementsHolderEntity<CtBlock<?>> {

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
        tagStatements();
        tagSourceCode();
        tagLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    @Override
    public List<StatementEntity<?>> getStatements() {
        List<CtStatement> statements = getElement().getStatements();
        List<StatementEntity<?>> result = new ArrayList<>();
        int size = statements.size();

        for (int i = 0; i < size; i++) {
            StatementEntity<?> statement = getFactory().wrap(statements.get(i));
            statement.setPosition(i);
            statement.setParent(this);
        }

        return result;
    }

    @Override
    public void tagStatements() {
        new StatementsTagger(this).tagStatements();
    }
}
