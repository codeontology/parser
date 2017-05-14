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

package org.codeontology.extraction.declaration;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.support.ModifiableEntity;
import org.codeontology.extraction.support.Modifier;

import java.util.List;

public class ModifiableTagger {

    ModifiableEntity<?> modifiable;

    public ModifiableTagger(ModifiableEntity modifiable) {
        this.modifiable = modifiable;
    }

    public void tagModifiers() {
        List<Modifier> modifiers = modifiable.getModifiers();

        for (Modifier modifier : modifiers) {
            /*Property modifierProperty;
            if (modifier.isAccessModifier()) {
                modifierProperty = Ontology.VISIBILITY_PROPERTY;
            } else {
                modifierProperty = Ontology.MODIFIER_PROPERTY;
            }*/

            RDFLogger.getInstance().addTriple(modifiable, Ontology.MODIFIER_PROPERTY, modifier.getIndividual());
        }
    }
}