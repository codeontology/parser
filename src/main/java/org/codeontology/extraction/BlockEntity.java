package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

import java.util.List;

public class BlockEntity extends StatementEntity<CtBlock> {

    public BlockEntity(CtBlock element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.BLOCK_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagStatements();
        tagEndLine();
    }

    public void tagEndLine() {
        int endLine = getElement().getPosition().getEndLine();
        Literal literal = getModel().createTypedLiteral(endLine);
        getLogger().addTriple(this, Ontology.END_LINE_PROPERTY, literal);
    }

    public void tagStatements() {
        List<CtStatement> statements = getElement().getStatements();
        int size = statements.size();
        for (int i = 0; i < size; i++) {
            StatementEntity<?> statement = getFactory().wrap(statements.get(i));
            statement.setPosition(i);
            statement.setParent(this.getParent());
            getLogger().addTriple(this, Ontology.STATEMENT_PROPERTY, statement);
            statement.extract();
        }
    }
}
