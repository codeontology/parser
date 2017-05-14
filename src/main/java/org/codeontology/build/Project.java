/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

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