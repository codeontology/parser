package org.codeontology.buildsystems;

import org.codeontology.extraction.ProjectVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class DefaultProject extends Project {

    public DefaultProject(File projectDirectory) {
        super(projectDirectory);
        if (projectDirectory == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected Collection<Project> findSubProjects() {
        return new ArrayList<>();
    }

    @Override
    public File getBuildFile() {
        return null;
    }

    @Override
    public DependenciesLoader<? extends DefaultProject> getLoader() {
        return new DefaultLoader(this);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
