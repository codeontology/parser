package org.codeontology.extraction.project;

import org.codeontology.extraction.EntityFactory;
import org.codeontology.projects.DefaultProject;
import org.codeontology.projects.gradle.AndroidProject;
import org.codeontology.projects.gradle.GradleProject;
import org.codeontology.projects.maven.MavenProject;

public class ProjectVisitor {

    private ProjectEntity<?> lastEntity;

    public void visit(DefaultProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(GradleProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(MavenProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(AndroidProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public ProjectEntity<?> getLastEntity() {
        return lastEntity;
    }
}
