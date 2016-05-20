package org.codeontology.buildsystems.gradle;

import org.codeontology.buildsystems.DependenciesLoader;
import org.codeontology.buildsystems.Project;
import org.codeontology.extraction.ProjectVisitor;

import java.io.File;

public class AndroidProject extends GradleProject {

    private AndroidLoader loader;

    public AndroidProject(File project) {
        super(project);
    }

    @Override
    public DependenciesLoader<? extends GradleProject> getLoader() {
        if (loader == null) {
            loader = new AndroidLoader(this);
        }
        return loader;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
