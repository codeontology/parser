package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.Ontology;

import java.util.List;

public class ModifiableTagger {

    ModifiableWrapper modifiable;

    public ModifiableTagger(ModifiableWrapper modifiable) {
        this.modifiable = modifiable;
    }

    public void tagModifiers() {
        List<ModifierClass> modifiers = modifiable.getModifiers();

        for (ModifierClass modifier : modifiers) {
            Property modifierProperty;
            if (modifier.isAccessModifier()) {
                modifierProperty = Ontology.VISIBILITY_PROPERTY;
            } else {
                modifierProperty = Ontology.MODIFIER_PROPERTY;
            }

            RDFLogger.getInstance().addTriple((Wrapper<?>) modifiable, modifierProperty, modifier.getIndividual());
        }
    }
}
