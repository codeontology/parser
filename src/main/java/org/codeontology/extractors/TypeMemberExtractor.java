package org.codeontology.extractors;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtReference;

public abstract class TypeMemberExtractor<E extends CtTypedElement & CtTypeMember & CtNamedElement> extends TypedElementExtractor<E> {

    private ModifiableTagger tagger;

    public TypeMemberExtractor(E member) {
        super(member);
        tagger = new ModifiableTagger(this);
    }

    public TypeMemberExtractor(CtReference reference) {
        super(reference);
    }

    protected void tagDeclaringType() {
        if (getElement() == null) {
            return;
        }

        Extractor declaringType = getFactory().getExtractor(getElement().getDeclaringType());
        addTriple(this, Ontology.DECLARED_BY_PROPERTY, declaringType.getResource());
    }

    protected void tagModifier() {
        if (isDeclarationAvailable()) {
            tagger.tagModifier();
        }
    }

    protected void tagVisibility() {
        if (isDeclarationAvailable()) {
            tagger.tagVisibility();
        }
    }
}
