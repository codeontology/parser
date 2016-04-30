package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

import java.util.List;


public abstract class AbstractWrapper<E extends CtNamedElement> implements Wrapper<E> {

    private E element;
    private CtReference reference;
    public static Model model = RDFLogger.getInstance().getModel();
    private Wrapper<?> parent;
    private String uri;

    public AbstractWrapper(E element) {
        setElement(element);
    }

    public AbstractWrapper(CtReference reference) {
        setReference(reference);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        this.element = (E) reference.getDeclaration();
    }

    @Override
    public E getElement() {
        return element;
    }

    @Override
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

    public Resource getResource() {
        return model.createResource(Ontology.WOC + getRelativeURI());
    }

    protected abstract String buildRelativeURI();

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

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public WrapperFactory getFactory() {
        return WrapperFactory.getInstance();
    }

    @Override
    public CtReference getReference() {
        return reference;
    }

    @Override
    public boolean isDeclarationAvailable() {
        return getElement() != null;
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
        if ((!isDeclarationAvailable() && !CodeOntology.isJarExplorationEnabled() || this instanceof ParameterizedTypeWrapper)
                && WrapperRegister.getInstance().add(this)) {
            extract();
        }
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

