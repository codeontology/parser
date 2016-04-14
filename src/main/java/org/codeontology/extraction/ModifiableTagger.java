package org.codeontology.extraction;

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
            encapsulation = Ontology.DEFAULT_INDIVIDUAL;
        } else {
            switch (modifier) {
                case PUBLIC:
                    encapsulation = Ontology.PUBLIC_INDIVIDUAL;
                    break;
                case PRIVATE:
                    encapsulation = Ontology.PRIVATE_INDIVIDUAL;
                    break;
                case PROTECTED:
                    encapsulation = Ontology.PROTECTED_INDIVIDUAL;
                    break;
                default:
                    encapsulation = Ontology.DEFAULT_INDIVIDUAL;
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
                    modifier = Ontology.ABSTRACT_INDIVIDUAL;
                    break;
                case FINAL:
                    modifier = Ontology.FINAL_INDIVIDUAL;
                    break;
                case STATIC:
                    modifier = Ontology.STATIC_INDIVIDUAL;
                    break;
                case SYNCHRONIZED:
                    modifier = Ontology.SYNCHRONIZED_INDIVIDUAL;
                    break;
                case TRANSIENT:
                    break;
                case VOLATILE:
                    modifier = Ontology.VOLATILE_INDIVIDUAL;
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
