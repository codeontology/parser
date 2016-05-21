package org.codeontology.projects.gradle;

import org.codeontology.extraction.ProjectVisitor;
import org.codeontology.projects.DependenciesLoader;

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
