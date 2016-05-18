package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtTry;

import java.util.ArrayList;
import java.util.List;

public class TryEntity extends StatementEntity<CtTry> implements BodyHolderEntity<CtTry> {

    public TryEntity(CtTry element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.TRY_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagBody();
        tagCatches();
        tagFinally();
        tagResources();
    }

    public void tagCatches() {
        List<CatchEntity> catches = getCatches();
        for (CatchEntity catchEntity : catches) {
            getLogger().addTriple(this, Ontology.CATCH_CLAUSE_PROPERTY, catchEntity);
            catchEntity.extract();
        }
    }

    public void tagFinally() {
        FinallyEntity finallyBlock = getFinally();
        if (finallyBlock != null) {
            getLogger().addTriple(this, Ontology.FINALLY_CLAUSE_PROPERTY, finallyBlock);
            finallyBlock.extract();
        }
    }

    public void tagResources() {

    }

    private List<CatchEntity> getCatches() {
        List<CatchEntity> catches = new ArrayList<>();
        List<CtCatch> catchers = getElement().getCatchers();
        int size = catchers.size();

        for (int i = 0; i < size; i++) {
            CatchEntity catchEntity = getFactory().wrap(catchers.get(i));
            catchEntity.setPosition(i);
            catchEntity.setParent(this);
            catches.add(catchEntity);
        }

        return catches;
    }

    public FinallyEntity getFinally() {
        CtBlock<?> block = getElement().getFinalizer();
        if (block != null) {
            FinallyEntity finallyBlock = new FinallyEntity(block);
            finallyBlock.setParent(this);
            return finallyBlock;
        }

        return null;
    }

    @Override
    public StatementEntity<?> getBody() {
        StatementEntity<?> body = getFactory().wrap(getElement().getBody());
        body.setPosition(0);
        body.setParent(this);
        return body;
    }

    @Override
    public void tagBody() {
        new BodyTagger(this).tagBody();
    }
}
