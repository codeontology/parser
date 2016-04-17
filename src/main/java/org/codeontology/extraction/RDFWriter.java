package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RDFWriter {
    private static Model model = Ontology.getModel();
    private static String outputFile = "triples.nt";

    public static Model getModel() {
        return model;
    }

    public static void setOutputFile(String path) {
        outputFile = path;
    }

    public static void writeRDF() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
            getModel().write(writer, "N-TRIPLE");
            model = Ontology.getModel();
        } catch (IOException e) {
            System.out.println("Unable to write triples");
            System.exit(-1);
        }
    }

    public static void addTriple(Wrapper subject, Property property, Wrapper object) {
        RDFWriter.addTriple(subject, property, object.getResource());
    }

    public static void addTriple(Wrapper subject, Property property, RDFNode object) {
        if (property != null && object != null) {
            model.add(model.createStatement(subject.getResource(), property, object));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
