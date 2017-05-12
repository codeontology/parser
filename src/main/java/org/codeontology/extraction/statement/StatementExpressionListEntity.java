package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.AbstractEntity;
import org.codeontology.extraction.Entity;

import java.util.List;

public class StatementExpressionListEntity extends AbstractEntity<List<Entity<?>>> {

    private int position;

    private static final String TAG = "statement-expression-list";

    public StatementExpressionListEntity(List<Entity<?>> list) {
        super(list);
        for (Entity<?> current : list) {
            current.setParent(this);
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
    }

    @Override
    public String getSourceCode() {
        List<Entity<?>> list = getElement();
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
