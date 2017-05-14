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

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.support.LineTagger;
import spoon.reflect.code.CtStatement;

public class StatementEntity<E extends CtStatement> extends CodeElementEntity<E> {
    private int position;

    public StatementEntity(E element) {
        super(element);
    }

    @Override
    public String buildRelativeURI() {
        return super.buildRelativeURI("statement");
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.STATEMENT_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagPosition();
        tagLine();
        tagSourceCode();
        tagLabel();
    }

    public void tagLabel() {
        String labelString = getElement().getLabel();
        if (labelString != null) {
            Literal label = getModel().createTypedLiteral(labelString);
            getLogger().addTriple(this, Ontology.WOC_LABEL_PROPERTY, label);
        }
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    public void tagPosition() {
        Literal position = getModel().createTypedLiteral(getPosition());
        RDFLogger.getInstance().addTriple(this, Ontology.POSITION_PROPERTY, position);
    }
}