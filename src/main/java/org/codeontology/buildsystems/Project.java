package org.codeontology.buildsystems;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Project {

    private File projectDirectory;
    private Collection<Project> subProjects;
    private File root;

    public Project(File projectDirectory) {
        if (projectDirectory == null) {
            throw new IllegalArgumentException();
        }
        this.projectDirectory = projectDirectory;
        this.root = projectDirectory;
        subProjects = findSubProjects();
    }

    protected Collection<Project> findSubProjects() {
       return new ArrayList<>();
    }

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

    public File getBuildFile() {
        return null;
    }

    public String getPath() {
        return getProjectDirectory().getPath();
    }

    public DependenciesLoader<? extends Project> getLoader() {
        return new DefaultLoader(this);
    }

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

}
