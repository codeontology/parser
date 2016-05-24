package org.codeontology.extraction.statement;

import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.StatementsHolderEntity;
import org.codeontology.extraction.support.StatementsTagger;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;

import java.util.List;

public abstract class SwitchLabelEntity extends CodeElementEntity<CtCase<?>>
        implements StatementsHolderEntity<CtCase<?>> {

    private SwitchLabelEntity next;

    public SwitchLabelEntity(CtCase<?> label) {
        super(label);
    }

    @Override
    public void extract() {
        tagType();
        tagStatements();
        tagLine();
        tagEndLine();
        tagNext();
    }

    public void tagNext() {
        if (getNext() != null) {
            getLogger().addTriple(this, Ontology.NEXT_PROPERTY, next);
        }
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

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    public void tagEndLine() {
        new LineTagger(this).tagEndLine();
    }

    public SwitchLabelEntity getNext() {
        return next;
    }

    public void setNext(SwitchLabelEntity next) {
        this.next = next;
    }
}