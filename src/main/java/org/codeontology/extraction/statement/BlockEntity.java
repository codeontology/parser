package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.StatementsHolderEntity;
import org.codeontology.extraction.support.StatementsTagger;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

import java.util.List;

public class BlockEntity extends StatementEntity<CtBlock<?>> implements StatementsHolderEntity<CtBlock<?>> {

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
        new LineTagger(this).tagEndLine();
    }

    @Override
    public List<StatementEntity<?>> getStatements() {
        List<CtStatement> statements = getElement().getStatements();
        return new StatementsTagger(this).asEntities(statements);
    }

    @Override
    public void tagStatements() {
        new StatementsTagger(this).tagStatements();
    }
}
