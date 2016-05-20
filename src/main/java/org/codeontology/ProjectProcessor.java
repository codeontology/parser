package org.codeontology;

import org.codeontology.buildsystems.Project;
import org.codeontology.extraction.ProjectEntity;
import org.codeontology.extraction.ProjectVisitor;

public class ProjectProcessor {

    private ProjectEntity<?> project;

    public ProjectProcessor(Project project) {
        if (project != null) {
            ProjectVisitor visitor = new ProjectVisitor();
            project.accept(visitor);
            this.project = visitor.getLastEntity();
        }
    }

    public void process() {
        if (project != null) {
            project.extract();
        }
    }

    public ProjectEntity<?> getProjectEntity() {
        return project;
    }
}
