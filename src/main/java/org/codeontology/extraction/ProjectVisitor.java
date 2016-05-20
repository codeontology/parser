package org.codeontology.extraction;

import org.codeontology.buildsystems.DefaultProject;
import org.codeontology.buildsystems.gradle.AndroidProject;
import org.codeontology.buildsystems.gradle.GradleProject;
import org.codeontology.buildsystems.maven.MavenProject;

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
