package org.codeontology.extraction;

import org.codeontology.CodeOntology;
import org.codeontology.projects.ClasspathLoader;

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
            this.jarFile = new JarFile(path);
            ClasspathLoader.getInstance().load(path);
            systemErr = System.err;
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
