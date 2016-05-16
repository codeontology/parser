package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public abstract class AbstractWrapper<E extends CtElement> implements Wrapper<E> {

    private E element;
    private static Model model = RDFLogger.getInstance().getModel();
    private Wrapper<?> parent;
    private String uri;

    AbstractWrapper() {

    }

    AbstractWrapper(E element) {
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

    public void tagComment() {
        String comment = getElement().getDocComment();
        if (comment != null) {
            getLogger().addTriple(this, Ontology.COMMENT_PROPERTY, model.createLiteral(comment));
        }
    }

    public void tagAnnotations() {
        List<CtAnnotation<?>> annotations = getElement().getAnnotations();
        for (CtAnnotation annotation : annotations) {
            TypeWrapper annotationType = getFactory().wrap(annotation.getAnnotationType());
            getLogger().addTriple(this, Ontology.ANNOTATION_PROPERTY, annotationType);
            annotationType.follow();
        }
    }

    protected abstract RDFNode getType();

    public void tagSourceCode() {
        getLogger().addTriple(this, Ontology.SOURCE_CODE_PROPERTY, model.createLiteral(getElement().toString()));
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public WrapperFactory getFactory() {
        return WrapperFactory.getInstance();
    }

    @Override
    public RDFLogger getLogger() {
        return RDFLogger.getInstance();
    }

    @Override
    public Wrapper<?> getParent() {
        return parent;
    }

    @Override
    public void setParent(Wrapper<?> parent) {
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
        if (!(object instanceof Wrapper<?>)) {
            return false;
        }

        Wrapper<?> other = (Wrapper<?>) object;
        return other.getRelativeURI().equals(this.getRelativeURI());
    }

    @Override
    public int hashCode() {
        return getRelativeURI().hashCode();
    }
}
