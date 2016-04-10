package org.codeontology.buildsystems;

public abstract class DependenciesLoader {

    private ClasspathLoader loader = ClasspathLoader.getInstance();

    public abstract void loadDependencies();

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
