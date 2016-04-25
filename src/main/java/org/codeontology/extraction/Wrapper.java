package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

import java.util.List;


public abstract class Wrapper<E extends CtNamedElement> {

    private E element;
    private CtReference reference;
    public static final String SEPARATOR = "-";
    public static Model model = RDFLogger.getInstance().getModel();
    private Wrapper<?> parent;

    public Wrapper(E element) {
        setElement(element);
    }

    public Wrapper(CtReference reference) {
        setReference(reference);
    }

    @SuppressWarnings("unchecked")
    public void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        this.element = (E) reference.getDeclaration();
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        this.element = element;
        try {
            this.reference = element.getReference();
        } catch (ClassCastException e) {
            this.reference = null;
        }
    }

    public abstract void extract();

    protected Resource getResource() {
        return model.createResource(Ontology.WOC + getRelativeURI());
    }

    public abstract String getRelativeURI();

    private RDFNode getName() {
        return model.createLiteral(getReference().getSimpleName());
    }

    public void tagType() {
        getLogger().addTriple(this, Ontology.RDF_TYPE_PROPERTY, getType());
    }

    public void tagName() {
        getLogger().addTriple(this, Ontology.NAME_PROPERTY, getName());
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

    public Model getModel() {
        return model;
    }

    public WrapperFactory getFactory() {
        return WrapperFactory.getInstance();
    }

    public CtReference getReference() {
        return reference;
    }

    public boolean isDeclarationAvailable() {
        return getElement() != null;
    }

    public RDFLogger getLogger() {
        return RDFLogger.getInstance();
    }

    public Wrapper<?> getParent() {
        return parent;
    }

    public void setParent(Wrapper<?> parent) {
        this.parent = parent;
    }

    public void follow() {
        if (!isDeclarationAvailable() && WrapperRegister.getInstance().add(this)) {
            extract();
        }
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

