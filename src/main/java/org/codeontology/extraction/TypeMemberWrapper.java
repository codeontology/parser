package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtReference;

public abstract class TypeMemberWrapper<E extends CtTypedElement & CtTypeMember & CtNamedElement> extends Wrapper<E> {

    private ModifiableTagger tagger;

    public TypeMemberWrapper(E member) {
        super(member);
        tagger = new ModifiableTagger(this);
    }

    public TypeMemberWrapper(CtReference reference) {
        super(reference);
    }

    protected void tagDeclaringType() {
        if (getElement() == null) {
            return;
        }

        Wrapper declaringType = getFactory().wrap(getElement().getDeclaringType());
        getLogger().addTriple(this, Ontology.DECLARED_BY_PROPERTY, declaringType.getResource());
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
