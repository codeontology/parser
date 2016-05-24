package org.codeontology.build;

import org.codeontology.extraction.project.ProjectVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public abstract class Project {
    protected File projectDirectory;
    protected Collection<Project> subProjects;
    protected File root;

    public Project(File projectDirectory) {
        this.projectDirectory = projectDirectory;
        this.root = projectDirectory;
        subProjects = findSubProjects();
    }

    protected abstract Collection<Project> findSubProjects();

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public Collection<Project> getSubProjects() {
        return subProjects;
    }

    public ProjectFactory getFactory() {
        return ProjectFactory.getInstance();
    }

    public String getBuildFileContent() {
        if (getBuildFile() != null) {
            try (Scanner scanner = new Scanner(getBuildFile())) {
                scanner.useDelimiter("\\Z");
                String build = "";
                if (scanner.hasNext()) {
                    build = scanner.next();
                }
                scanner.close();
                return build;
            } catch (FileNotFoundException e) {
                return "";
            }
        }

        return "";
    }

    public abstract File getBuildFile();

    public String getPath() {
        return getProjectDirectory().getPath();
    }

    public abstract DependenciesLoader<? extends Project> getLoader();

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    protected Collection<Project> initSubProjects(Collection<File> files) {
        List<Project> result = new ArrayList<>();
        for (File file : files) {
            Project subProject = getFactory().getProject(file);
            subProject.setRoot(getRoot());
            result.add(subProject);
        }

        return result;
    }

    public String getName() {
        return getProjectDirectory().getName();
    }

    public abstract void accept(ProjectVisitor visitor);
}
