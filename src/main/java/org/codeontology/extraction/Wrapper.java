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
    public static Model model = RDFWriter.getModel();

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

    protected abstract String getRelativeURI();

    protected RDFNode getName() {
        return model.createLiteral(getReference().getSimpleName());
    }

    protected void tagType() {
        RDFWriter.addTriple(this, Ontology.RDF_TYPE_PROPERTY, getType());
    }

    protected void tagName() {
        RDFWriter.addTriple(this, Ontology.NAME_PROPERTY, getName());
    }

    protected void tagComment() {
        String comment = getElement().getDocComment();
        if (comment != null) {
            RDFWriter.addTriple(this, Ontology.COMMENT_PROPERTY, model.createLiteral(comment));
        }
    }

    protected void tagAnnotations() {
        List<CtAnnotation<?>> annotations = getElement().getAnnotations();
        for (CtAnnotation annotation : annotations) {
            TypeWrapper annotationType = getFactory().wrap(annotation.getAnnotationType());
            RDFWriter.addTriple(this, Ontology.ANNOTATION_PROPERTY, annotationType);
            if (!annotationType.isDeclarationAvailable()) {
                annotationType.extract();
            }
        }
    }

    protected abstract RDFNode getType();

    protected void tagSourceCode() {
        RDFWriter.addTriple(this, Ontology.SOURCE_CODE_PROPERTY, model.createLiteral(getElement().toString()));
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

}

