package org.codeontology.buildsystems;

import java.io.File;
import java.util.Set;

public abstract class DependenciesLoader {

    private ClasspathLoader loader = ClasspathLoader.getInstance();

    public abstract void loadDependencies();

    public Set<File> getJarsLoaded() {
        return getLoader().getJarsLoaded();
    }

    public void loadClasspath(String classpath) {
        loader.loadClasspath(classpath);
    }

    public ClasspathLoader getLoader() {
        return loader;
    }

    public LoaderFactory getFactory() {
        return LoaderFactory.getInstance();
    }
}
