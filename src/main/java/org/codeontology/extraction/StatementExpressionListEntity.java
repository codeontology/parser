package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;

import java.util.List;

public class StatementExpressionListEntity extends AbstractEntity<List<StatementEntity<?>>>
        implements StatementsHolderEntity<List<StatementEntity<?>>> {

    private int position;

    private static final String TAG = "statement-expression-list";

    public StatementExpressionListEntity(List<StatementEntity<?>> list) {
        super(list);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StatementEntity<?> current = list.get(i);
            current.setParent(this);
            current.setPosition(i);
        }
    }

    @Override
    protected String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.STATEMENT_EXPRESSION_LIST_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagStatements();
    }

    @Override
    public List<StatementEntity<?>> getStatements() {
        return getElement();
    }

    public void tagStatements() {
        new StatementsTagger(this).tagStatements();
    }

    @Override
    public String getSourceCode() {
        List<StatementEntity<?>> list = getElement();
        int size = list.size();
        StringBuilder builder = new StringBuilder();

        if (size > 0) {
            builder.append(list.get(0).getElement());
        }

        for (int i = 1; i < size; i++) {
            builder.append(", ");
            builder.append(list.get(i).getElement());
        }

        return builder.toString();
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
