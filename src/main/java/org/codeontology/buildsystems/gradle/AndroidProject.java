package org.codeontology.buildsystems.gradle;

import org.codeontology.buildsystems.DependenciesLoader;
import org.codeontology.buildsystems.Project;

import java.io.File;

public class AndroidProject extends GradleProject {

    private AndroidLoader loader;

    public AndroidProject(File project) {
        super(project);
    }

    @Override
    public DependenciesLoader<? extends Project> getLoader() {
        if (loader == null) {
            loader = new AndroidLoader(this);
        }
        return loader;
    }
}
