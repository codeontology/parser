package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class Extractor<E extends CtNamedElement> {

    private static Model model = Ontology.baseModel();
    private E element;
    private CtReference reference;
    public static final String SEPARATOR = "-";
    private static String outputFile = "output.nt";

    public Extractor(E element) {
        setElement(element);
    }

    public Extractor(CtReference reference) {
        setReference(reference);
    }

    public void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        this.element = (E) reference.getDeclaration();
    }

    public Model getModel() {
        return model;
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

    protected void addStatement(Property property, RDFNode object) {
        if (property != null && object != null) {
            model.add(model.createStatement(getResource(), property, object));
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected Resource getResource() {
        return model.createResource(Ontology.getBaseURI() + getRelativeURI());
    }

    protected abstract String getRelativeURI();

    protected RDFNode getName() {
        return model.createLiteral(getReference().getSimpleName());
    }

    protected void tagType() {
        addStatement(Ontology.getTypeProperty(), getType());
    }

    protected void tagName() {
        addStatement(Ontology.getNameProperty(), getName());
    }

    protected void tagComment() {
        String comment = getElement().getDocComment();
        if (comment == null) {
            comment = "";
        }
        addStatement(Ontology.getCommentProperty(), model.createLiteral(comment));
    }

    protected abstract RDFNode getType();

    protected void tagSourceCode() {
        addStatement(Ontology.getSourceCodeProperty(), getModel().createLiteral(getElement().toString()));
    }

    public ExtractorFactory getFactory() {
        return ExtractorFactory.getInstance();
    }

    public void writeRDF() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
            getModel().write(writer, "N-TRIPLE");
            model = Ontology.baseModel();
        } catch (IOException e) {
            System.out.println("Unable to write triples");
            System.exit(-1);
        }
    }

    public CtReference getReference() {
        return reference;
    }

    public boolean isDeclarationAvailable() {
        return getElement() != null;
    }

    public static void setOutputFile(String path) {
        outputFile = path;
    }
}

