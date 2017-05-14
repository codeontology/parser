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