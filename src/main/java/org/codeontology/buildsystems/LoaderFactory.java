package org.codeontology.buildsystems;

import org.codeontology.buildsystems.gradle.AndroidLoader;
import org.codeontology.buildsystems.gradle.GradleLoader;
import org.codeontology.buildsystems.maven.MavenLoader;

import java.io.File;

public class LoaderFactory {

    private static LoaderFactory instance;

    private LoaderFactory() {

    }

    public static LoaderFactory getInstance() {
        if (instance == null) {
            instance = new LoaderFactory();
        }

        return instance;
    }

    public DependenciesLoader getLoader(String path) {
        return getLoader(new File(path));
    }

    public DependenciesLoader getLoader(File project) {
        switch (BuildSystem.getBuildSystem(project)) {
            case MAVEN:
                return new MavenLoader(project);
            case GRADLE:
                if (new File(project.getPath() + "/src/main/AndroidManifest.xml").exists()) {
                    return new AndroidLoader(project);
                } else {
                    return new GradleLoader(project);
                }
        }

        return new DefaultLoader(project);
    }
}
