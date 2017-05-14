/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction.statement;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.LocalVariableEntity;
import org.codeontology.extraction.support.BodyHolderEntity;
import org.codeontology.extraction.support.BodyTagger;
import spoon.reflect.code.*;

import java.util.ArrayList;
import java.util.Iterator;
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
        Iterator<CatchEntity> iterator = getCatches().iterator();

        if (!iterator.hasNext()) {
            return;
        }

        CatchEntity current = iterator.next();
        getLogger().addTriple(this, Ontology.CATCH_CLAUSE_PROPERTY, current);
        current.extract();

        CatchEntity previous = current;

        while (iterator.hasNext()) {
            current = iterator.next();
            getLogger().addTriple(this, Ontology.CATCH_CLAUSE_PROPERTY, current);
            getLogger().addTriple(previous, Ontology.NEXT_PROPERTY, current);
            current.extract();
            previous = current;
        }
    }

    public void tagFinally() {
        FinallyEntity finallyBlock = getFinally();
        if (finallyBlock != null) {
            getLogger().addTriple(this, Ontology.FINALLY_CLAUSE_PROPERTY, finallyBlock);
            finallyBlock.extract();
        }
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

    public void tagResources() {
        List<LocalVariableEntity> resources = getResources();
        for (LocalVariableEntity resource : resources) {
            getLogger().addTriple(this, Ontology.RESOURCE_PROPERTY, resource);
            resource.extract();
        }
    }

    public List<LocalVariableEntity> getResources() {
        List<LocalVariableEntity> result = new ArrayList<>();

        if (getElement() instanceof CtTryWithResource) {
            CtTryWithResource tryWithResources = (CtTryWithResource) getElement();
            List<CtLocalVariable<?>> resources = tryWithResources.getResources();

            for (CtLocalVariable<?> resource : resources) {
                LocalVariableEntity variable = getFactory().wrap(resource);
                variable.setParent(this);
                result.add(variable);
            }

        }

        return result;
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