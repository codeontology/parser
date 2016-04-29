package org.codeontology.extraction;

import org.codeontology.buildsystems.ClasspathLoader;
import spoon.reflect.reference.CtPackageReference;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarProcessor {
    private JarFile jarFile;
    private Map<Package, Set<Class<?>>> map;
    private PrintStream systemErr;

    public JarProcessor(String path) throws IOException {
        this.jarFile = new JarFile(path);
        ClasspathLoader.getInstance().load(path);
        systemErr = System.err;
    }

    public JarProcessor(File jar) throws IOException {
        this(jar.getPath());
    }


    public void process() {
        try {
            hideMessages();
            buildMap();
            extractAllTriples();
        } finally {
            System.setErr(systemErr);
        }
    }

    private void buildMap() {
        System.out.println("Analyzing file " + jarFile.getName() + "...");
        Enumeration entries = jarFile.entries();
        map = new HashMap<>();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String entryPath = entry.getName();
            if (entryPath.endsWith(".class") && !entryPath.contains("$")) {
                String typeName = entry.getName().replace("/", ".").substring(0, entryPath.length() - 6);
                try {
                    Class<?> clazz = Class.forName(typeName);
                    Package pack = clazz.getPackage();
                    Set<Class<?>> types = map.get(pack);
                    if (pack != null) {
                        if (types == null) {
                            types = new HashSet<>();
                        }
                        types.add(clazz);
                        map.put(pack, types);
                    }
                } catch (Throwable e) {
                    // it was not possible to get a class object from this jar entry
                    // we just skip this entry
                }
            }
        }
    }

    private void hideMessages() {
        PrintStream tmpErr = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
        System.setErr(tmpErr);
    }


    private void extractAllTriples() {
        System.out.println("Extracting triples for " + jarFile.getName() + "...");
        Set<Package> packages = map.keySet();
        for (Package pack : packages) {
            CtPackageReference packageReference = ReflectionFactory.getInstance().createPackageReference(pack);
            PackageWrapper wrapper = WrapperFactory.getInstance().wrap(packageReference);
            wrapper.setTypes(map.get(pack));
            wrapper.extract();
        }
        RDFLogger.getInstance().writeRDF();
        System.out.println("Triples extracted successfully.");
    }
}
