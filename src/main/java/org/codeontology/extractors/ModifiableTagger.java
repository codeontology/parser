package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;

import java.util.Set;

public class ModifiableTagger {

    Extractor<? extends CtModifiable> extractor;

    public ModifiableTagger(Extractor<? extends CtModifiable> extractor) {
        this.extractor = extractor;
    }

    protected void tagVisibility() {
        ModifierKind modifier = extractor.getElement().getVisibility();
        Resource encapsulation;

        if (modifier == null) {
            encapsulation = Ontology.DEFAULT_RESOURCE;
        } else {
            switch (modifier) {
                case PUBLIC:
                    encapsulation = Ontology.PUBLIC_RESOURCE;
                    break;
                case PRIVATE:
                    encapsulation = Ontology.PRIVATE_RESOURCE;
                    break;
                case PROTECTED:
                    encapsulation = Ontology.PROTECTED_RESOURCE;
                    break;
                default:
                    encapsulation = Ontology.DEFAULT_RESOURCE;
                    break;
            }
        }

        extractor.addTriple(extractor, Ontology.VISIBILITY_PROPERTY, encapsulation);
    }

    protected void tagModifier() {
        Set<ModifierKind> modifiers = extractor.getElement().getModifiers();
        Resource modifier;

        for (ModifierKind current : modifiers) {
            modifier = null;
            switch (current) {
                case ABSTRACT:
                    modifier = Ontology.ABSTRACT_RESOURCE;
                    break;
                case FINAL:
                    modifier = Ontology.FINAL_RESOURCE;
                    break;
                case STATIC:
                    modifier = Ontology.STATIC_RESOURCE;
                    break;
                case SYNCHRONIZED:
                    modifier = Ontology.SYNCHRONIZED_RESOURCE;
                    break;
                case TRANSIENT:
                    break;
                case VOLATILE:
                    modifier = Ontology.VOLATILE_RESOURCE;
                    break;
                case NATIVE:
                    break;
                case STRICTFP:
                    break;
                default:
                    break;
            }
            if (modifier != null) {
                extractor.addTriple(extractor, Ontology.MODIFIER_PROPERTY, modifier);
            }
        }
    }
}
