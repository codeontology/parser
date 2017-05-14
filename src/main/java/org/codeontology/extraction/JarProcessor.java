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

import org.codeontology.CodeOntology;
import org.codeontology.build.ClasspathLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.jar.JarFile;

public class JarProcessor {
    private JarFile jarFile;
    private PrintStream systemErr;

    public JarProcessor(String path) {
        try {
            if (new File(path).exists()) {
                this.jarFile = new JarFile(path);
                ClasspathLoader.getInstance().load(path);
                systemErr = System.err;
            }
        } catch (Exception | Error e) {
            CodeOntology.showWarning("Could not access file " + path);
        }
    }

    public JarProcessor(File jar) {
        this(jar.getPath());
    }

    public void process() {
        try {
            try {
                hideMessages();
                EntityFactory.getInstance().wrap(jarFile).extract();
            } finally {
                System.setErr(systemErr);
            }
        } catch (Exception | Error e) {
            CodeOntology.getInstance().handleFailure(e);
        }
    }

    private void hideMessages() {
        PrintStream tmpErr = new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {

            }
        });
        System.setErr(tmpErr);
    }
}