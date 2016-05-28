package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.StatementsHolderEntity;
import org.codeontology.extraction.support.StatementsTagger;
import spoon.reflect.code.CtBlock;

import java.util.List;

public class FinallyEntity extends CodeElementEntity<CtBlock<?>> implements StatementsHolderEntity<CtBlock<?>> {

    public FinallyEntity(CtBlock<?> block) {
        super(block);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FINALLY_CLAUSE;
    }

    @Override
    public void extract() {
        tagStatements();
        tagSourceCode();
        tagLine();
        tagEndLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    @Override
    public List<StatementEntity<?>> getStatements() {
        return new StatementsTagger(this).asEntities(getElement().getStatements());
    }

    @Override
    public void tagStatements() {
        new StatementsTagger(this).tagStatements();
    }

    public void tagEndLine() {
        new LineTagger(this).tagEndLine();
    }
}
