package org.codeontology.extraction.project;

import org.codeontology.extraction.EntityFactory;
import org.codeontology.build.DefaultProject;
import org.codeontology.build.gradle.AndroidProject;
import org.codeontology.build.gradle.GradleProject;
import org.codeontology.build.maven.MavenProject;

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
