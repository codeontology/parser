package org.codeontology.buildsystems;

public class DefaultLoader extends DependenciesLoader<Project> {

    private Project project;

    public DefaultLoader(Project project) {
        super(project);
        this.project = project;
    }

    @Override
    public void loadDependencies() {
        getLoader().loadAllJars(project.getRoot());
    }
}
