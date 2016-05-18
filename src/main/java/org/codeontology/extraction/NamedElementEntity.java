package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

public abstract class NamedElementEntity<E extends CtNamedElement> extends CodeElementEntity<E> {

    private CtReference reference;

    NamedElementEntity(E element) {
        setElement(element);
    }

    NamedElementEntity(CtReference reference) {
        setReference(reference);
    }

    @SuppressWarnings("unchecked")
    private void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        if (reference.getDeclaration() != null) {
            setElement((E) reference.getDeclaration());
        }
    }

    @Override
    protected void setElement(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        super.setElement(element);
        try {
            this.reference = element.getReference();
        } catch (ClassCastException e) {
            this.reference = null;
        }
    }

    public CtReference getReference() {
        return reference;
    }

    private RDFNode getName() {
        return getModel().createLiteral(getReference().getSimpleName());
    }

    public void tagName() {
        getLogger().addTriple(this, Ontology.NAME_PROPERTY, getName());
    }

    @Override
    public void follow() {
        if (!isDeclarationAvailable() && !CodeOntology.isJarExplorationEnabled()
                && EntityRegister.getInstance().add(this)) {
            extract();
        }
    }

    public boolean isDeclarationAvailable() {
        return getElement() != null;
    }
}

