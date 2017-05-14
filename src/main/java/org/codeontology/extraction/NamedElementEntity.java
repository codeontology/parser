/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

public abstract class NamedElementEntity<E extends CtNamedElement> extends CodeElementEntity<E> {

    private CtReference reference;

    protected NamedElementEntity(E element) {
        setElement(element);
    }

    protected NamedElementEntity(CtReference reference) {
        setReference(reference);
    }

    @SuppressWarnings("unchecked")
    private void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        if (reference.getDeclaration() != null && getElement() == null) {
            setElement((E) reference.getDeclaration());
        }
    }

    @Override
    protected void setElement(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        super.setElement(element);
        if (reference == null) {
            try {
                this.reference = element.getReference();
            } catch (ClassCastException e) {
                // leave reference null
            }
        }
    }

    public CtReference getReference() {
        return reference;
    }

    public String getName() {
        return getReference().getSimpleName();
    }

    public void tagName() {
        Literal name = getModel().createTypedLiteral(getName());
        getLogger().addTriple(this, Ontology.NAME_PROPERTY, name);
    }

    public void tagLabel() {
        String labelString = splitCamelCase(getName());
        Literal label = getModel().createTypedLiteral(labelString);
        getLogger().addTriple(this, Ontology.RDFS_LABEL_PROPERTY, label);
    }

    public String splitCamelCase(String s) {
        return s.replaceAll(
            String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ), " "
        );
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