package org.codeontology.build.gradle;

import org.codeontology.extraction.project.ProjectVisitor;
import org.codeontology.build.DependenciesLoader;

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
