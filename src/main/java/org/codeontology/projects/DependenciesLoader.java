package org.codeontology.projects;

import java.io.File;
import java.util.Set;

public abstract class DependenciesLoader<T extends Project> {

    private T project;

    public DependenciesLoader(T project) {
        this.project = project;
    }

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

    public T getProject() {
        return project;
    }


}
