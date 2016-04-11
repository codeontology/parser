package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtReference;

import java.util.Set;

public abstract class TypeMemberExtractor<E extends CtTypedElement & CtTypeMember & CtNamedElement> extends TypedElementExtractor<E> {

    public TypeMemberExtractor(E member) {
        super(member);
    }

    public TypeMemberExtractor(CtReference reference) {
        super(reference);
    }

    protected void tagDeclaringType() {
        if (getElement() == null) {
            return;
        }

        Extractor declaringType = getFactory().getExtractor(getElement().getDeclaringType());
        addStatement(Ontology.DECLARED_BY_PROPERTY, declaringType.getResource());
    }

    protected void tagEncapsulation() {
        ModifierKind modifier = getElement().getVisibility();
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

        addStatement(Ontology.VISIBILITY_PROPERTY, encapsulation);
    }

    protected void tagModifier() {
        Set<ModifierKind> modifiers = getElement().getModifiers();
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
                addStatement(Ontology.MODIFIER_PROPERTY, modifier);
            }
        }
    }
}
