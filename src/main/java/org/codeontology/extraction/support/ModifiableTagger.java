package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;

import java.util.List;

public class ModifiableTagger {

    ModifiableEntity<?> modifiable;

    public ModifiableTagger(ModifiableEntity modifiable) {
        this.modifiable = modifiable;
    }

    public void tagModifiers() {
        List<Modifier> modifiers = modifiable.getModifiers();

        for (Modifier modifier : modifiers) {
            Property modifierProperty;
            if (modifier.isAccessModifier()) {
                modifierProperty = Ontology.VISIBILITY_PROPERTY;
            } else {
                modifierProperty = Ontology.MODIFIER_PROPERTY;
            }

            RDFLogger.getInstance().addTriple(modifiable, modifierProperty, modifier.getIndividual());
        }
    }
}
