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
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

public class ClassDeclarationStatement extends StatementEntity<CtClass<?>> {
    public ClassDeclarationStatement(CtClass<?> element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_DECLARATION_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagDeclaredClass();
    }

    public TypeEntity<?> getDeclaredClass() {
        TypeEntity<?> type = getFactory().wrap((CtType<?>) getElement());
        type.setParent(getParent(ExecutableEntity.class, TypeEntity.class));
        return type;
    }

    private void tagDeclaredClass() {
        TypeEntity<?> declaredClass = getDeclaredClass();
        getLogger().addTriple(declaredClass, Ontology.DECLARATION_PROPERTY, this);
        declaredClass.extract();
    }
}