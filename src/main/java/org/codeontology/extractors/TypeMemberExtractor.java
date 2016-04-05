package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.Property;
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
        addStatement(Ontology.getDeclaredByProperty(), declaringType.getResource());
    }

    protected void tagEncapsulation() {
        ModifierKind modifier = getElement().getVisibility();
        Property encapsulation;

        if (modifier == null) {
            encapsulation = Ontology.getDefaultProperty();
        } else {
            switch (modifier) {
                case PUBLIC:
                    encapsulation = Ontology.getPublicProperty();
                    break;
                case PRIVATE:
                    encapsulation = Ontology.getPrivateProperty();
                    break;
                case PROTECTED:
                    encapsulation = Ontology.getProtectedProperty();
                    break;
                default:
                    encapsulation = Ontology.getDefaultProperty();
                    break;
            }
        }

        addStatement(Ontology.getEncapsulationProperty(), encapsulation);
    }

    protected void tagModifier() {
        Set<ModifierKind> modifiers = getElement().getModifiers();
        Property modifier;

        for (ModifierKind current : modifiers) {
            modifier = null;
            switch (current) {
                case ABSTRACT:
                    modifier = Ontology.getAbstractProperty();
                    break;
                case FINAL:
                    modifier = Ontology.getFinalProperty();
                    break;
                case STATIC:
                    modifier = Ontology.getStaticProperty();
                    break;
                case SYNCHRONIZED:
                    modifier = Ontology.getSynchronizedProperty();
                    break;
                case TRANSIENT:
                    break;
                case VOLATILE:
                    modifier = Ontology.getVolatileProperty();
                    break;
                case NATIVE:
                    break;
                case STRICTFP:
                    break;
                default:
                    break;
            }
            if (modifier != null) {
                addStatement(Ontology.getModifierProperty(), modifier);
            }
        }
    }
}
