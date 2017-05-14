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
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.StatementsHolderEntity;
import org.codeontology.extraction.support.StatementsTagger;
import spoon.reflect.code.CtCatch;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class CatchEntity extends CodeElementEntity<CtCatch> implements StatementsHolderEntity<CtCatch> {

    private int position;

    public CatchEntity(CtCatch catcher) {
        super(catcher);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CATCH_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagLine();
        tagStatements();
        tagEndLine();
        tagCatchFormalParameters();
    }

    public void tagCatchFormalParameters() {
        List<TypeEntity<?>> formalParameters = getCatchFormalParameters();
        for (TypeEntity<?> catchFormalParameter : formalParameters) {
            getLogger().addTriple(this, Ontology.CATCH_FORMAL_PARAMETER_PROPERTY, catchFormalParameter);
            catchFormalParameter.follow();
        }
    }

    public List<TypeEntity<?>> getCatchFormalParameters() {
        List<CtTypeReference<?>> references = getElement().getParameter().getMultiTypes();
        List<TypeEntity<?>> parameters = new ArrayList<>();

        for (CtTypeReference<?> reference : references) {
            TypeEntity<?> parameter = getFactory().wrap(reference);
            parameter.setParent(getParent(ExecutableEntity.class));
            parameters.add(parameter);
        }

        return parameters;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    @Override
    public List<StatementEntity<?>> getStatements() {
        return new StatementsTagger(this).asEntities(getElement().getBody().getStatements());
    }

    @Override
    public void tagStatements() {
        new StatementsTagger(this).tagStatements();
    }

    public void tagEndLine() {
        new LineTagger(this).tagEndLine();
    }


}