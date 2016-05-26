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
