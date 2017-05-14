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

package org.codeontology.extraction.support;

import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.EntityFactory;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.declaration.TypeVariableEntity;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class FormalTypeParametersTagger {
    private GenericDeclarationEntity<?> genericDeclaration;

    public FormalTypeParametersTagger(GenericDeclarationEntity<?> genericDeclaration) {
        this.genericDeclaration = genericDeclaration;
    }

    public void tagFormalTypeParameters() {
        if (!CodeOntology.processGenerics()) {
            return;
        }
        List<TypeVariableEntity> parameters = genericDeclaration.getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableEntity typeVariable = parameters.get(i);
            typeVariable.setParent(genericDeclaration);
            typeVariable.setPosition(i);
            RDFLogger.getInstance().addTriple(genericDeclaration, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, typeVariable);
            typeVariable.extract();
        }
    }

    public static List<TypeVariableEntity> formalTypeParametersOf(GenericDeclarationEntity<?> genericDeclaration) {
        List<TypeVariableEntity> typeVariables = new ArrayList<>();

        if (genericDeclaration.getElement() != null && CodeOntology.processGenerics()) {
            List<CtTypeReference<?>> parameters = genericDeclaration.getElement().getFormalTypeParameters();

            for (CtTypeReference parameter : parameters) {
                Entity<?> entity = EntityFactory.getInstance().wrap(parameter);
                if (entity instanceof TypeVariableEntity) {
                    typeVariables.add((TypeVariableEntity) entity);
                }
            }
        }

        return typeVariables;
    }
}