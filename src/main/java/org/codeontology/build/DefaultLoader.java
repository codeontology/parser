package org.codeontology.build;

public class DefaultLoader extends DependenciesLoader<DefaultProject> {

    private DefaultProject project;

    public DefaultLoader(DefaultProject project) {
        super(project);
        this.project = project;
    }

    @Override
    public void loadDependencies() {
        getLoader().loadAllJars(project.getRoot());
    }
}
