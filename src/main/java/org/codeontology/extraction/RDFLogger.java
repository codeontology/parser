package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import org.codeontology.Ontology;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RDFLogger {
    private Model model = Ontology.getModel();
    private String outputFile = "triples.nt";
    private int counter = 0;
    private static RDFLogger instance = new RDFLogger();
    public static final int LIMIT = 10000;

    private RDFLogger() {

    }

    public static RDFLogger getInstance() {
        return instance;
    }

    public Model getModel() {
        return model;
    }

    public void setOutputFile(String path) {
        outputFile = path;
    }

    public void writeRDF() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
            model.write(writer, "N-TRIPLE");
        } catch (IOException e) {
            System.out.println("Unable to write triples");
            System.exit(-1);
        }
    }

    public void addTriple(Wrapper subject, Property property, Wrapper object) {
        addTriple(subject, property, object.getResource());
    }

    public void addTriple(Wrapper subject, Property property, RDFNode object) {
        if (property != null && object != null) {
            Statement triple = model.createStatement(subject.getResource(), property, object);
            model.add(triple);
            counter++;
            if (counter > LIMIT) {
                writeRDF();
                free();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void free() {
        model = Ontology.getModel();
        counter = 0;
    }
}
