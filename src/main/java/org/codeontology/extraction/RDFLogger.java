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

import com.hp.hpl.jena.rdf.model.*;
import org.codeontology.Ontology;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RDFLogger {
    private Model model;
    private String outputFile;
    private int counter;
    private static RDFLogger instance;

    public static final int MAX_SIZE = 10000;

    private RDFLogger() {
        model = Ontology.getModel();
        outputFile = "triples.nt";
        counter = 0;
    }

    public static RDFLogger getInstance() {
        if (instance == null) {
            instance = new RDFLogger();
        }
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
            System.out.println("Cannot write triples.");
            System.exit(-1);
        }
    }

    public void addTriple(Entity<?> subject, Property property, Entity object) {
        addTriple(subject, property, object.getResource());
    }

    public void addTriple(Entity<?> subject, Property property, RDFNode object) {
        if (property != null && object != null) {
            Statement triple = model.createStatement(subject.getResource(), property, object);
            model.add(triple);
            counter++;
            if (counter > MAX_SIZE) {
                writeRDF();
                free();
            }
        }
    }

    private void free() {
        model = ModelFactory.createDefaultModel();
        counter = 0;
    }
}