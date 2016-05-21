package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;

public abstract class AbstractEntity<E> implements Entity<E> {

    private E element;
    private static Model model = RDFLogger.getInstance().getModel();
    private Entity<?> parent;
    private String uri;

    AbstractEntity() {

    }

    AbstractEntity(E element) {
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

    public Entity<?> getParent(Class<?> clazz) {
        Entity<?> parent = getParent();
        while (parent != null && !(clazz.isAssignableFrom(parent.getClass()))) {
            parent = parent.getParent();
        }

        return parent;
    }
}


