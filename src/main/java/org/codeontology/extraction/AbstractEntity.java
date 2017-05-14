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


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;

public abstract class AbstractEntity<E> implements Entity<E>, Comparable<Entity<?>> {

    private E element;
    private static Model model = RDFLogger.getInstance().getModel();
    private Entity<?> parent;
    private String uri;

    protected AbstractEntity() {

    }

    protected AbstractEntity(E element) {
        setElement(element);
    }

    @Override
    public E getElement() {
        return element;
    }

    protected void setElement(E element) {
        this.element = element;
    }

    public Resource getResource() {
        return model.createResource(Ontology.WOC + getRelativeURI());
    }

    protected abstract String buildRelativeURI();

    public void tagType() {
        getLogger().addTriple(this, Ontology.RDF_TYPE_PROPERTY, getType());
    }

    protected abstract RDFNode getType();

    public void tagSourceCode() {
        getLogger().addTriple(this, Ontology.SOURCE_CODE_PROPERTY, model.createLiteral(getSourceCode()));
    }

    public String getSourceCode() {
        return getElement().toString();
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public EntityFactory getFactory() {
        return EntityFactory.getInstance();
    }

    @Override
    public RDFLogger getLogger() {
        return RDFLogger.getInstance();
    }

    @Override
    public Entity<?> getParent() {
        return parent;
    }

    @Override
    public void setParent(Entity<?> parent) {
        this.parent = parent;
    }

    @Override
    public void follow() {
        extract();
    }

    @Override
    public final String getRelativeURI() {
        if (uri == null) {
            uri = buildRelativeURI();
        }

        return uri;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Entity<?>)) {
            return false;
        }

        Entity<?> other = (Entity<?>) object;
        return other.getRelativeURI().equals(this.getRelativeURI());
    }

    @Override
    public int hashCode() {
        return getRelativeURI().hashCode();
    }

    public Entity<?> getParent(Class<?>... classes) {
        Entity<?> parent = getParent();
        while (parent != null) {
            for (Class<?> currentClass : classes) {
                if (currentClass.isAssignableFrom(parent.getClass())) {
                    return parent;
                }
            }
            parent = parent.getParent();
        }

        return null;
    }

    public int compareTo(Entity<?> other) {
        return this.getRelativeURI().compareTo(other.getRelativeURI());
    }
}

