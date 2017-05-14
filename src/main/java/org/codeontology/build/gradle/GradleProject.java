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

package org.codeontology.build.gradle;

import org.codeontology.CodeOntology;
import org.codeontology.build.BuildFiles;
import org.codeontology.build.DependenciesLoader;
import org.codeontology.build.Project;
import org.codeontology.extraction.project.ProjectVisitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GradleProject extends Project {

    private File buildFile;
    private GradleLoader loader;
    private boolean setUp;
    private static final String SUBPROJECTS_FILE_NAME = "subProjects" + CodeOntology.SUFFIX;

    public GradleProject(File projectDirectory) {
        super(projectDirectory);
        setUp();
    }

    @Override
    protected Collection<Project> findSubProjects() {
        setUp();
        Set<File> subProjects = new HashSet<>();
        String task = "subprojects {\n" +
                "\ttask CodeOntologySub << {\n" +
                "\t\ttask -> new File(rootDir, \"" + SUBPROJECTS_FILE_NAME + "\").append(\"$task.project.projectDir\\n\");\n" +
                "\t}\n" +
                "}";
        File buildFile = getBuildFile();
        String content = getBuildFileContent();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(buildFile, true))) {
            if (!content.contains(task)) {
                writer.write("\n\n" + task);
                writer.close();
            }

            loader.runTask("CodeOntologySub");

            File subProjectsFile = new File(getRoot() + "/" + SUBPROJECTS_FILE_NAME);

            try (Scanner scanner = new Scanner(subProjectsFile)) {
                while (scanner.hasNextLine()) {
                    subProjects.add(new File(scanner.nextLine()));
                }
            }

            if (!subProjects.isEmpty()) {
                System.out.println("Subprojects of " + getProjectDirectory().getName() + ":");
                for (File file : subProjects) {
                    System.out.println(file.getName());
                }
            }
        } catch (IOException e) {
            System.out.println("No subproject found for project " + getProjectDirectory().getName() + ".");
        }

        return initSubProjects(subProjects);
    }

    @Override
    protected Collection<Project> initSubProjects(Collection<File> files) {
        Collection<Project> result =  super.initSubProjects(files);
        removeSubProjectsFile();
        return result;
    }

    private void removeSubProjectsFile() {
        File subProjectsFile = new File(getRoot() + "/" + SUBPROJECTS_FILE_NAME);
        if (!subProjectsFile.exists()) {
            return;
        }

        boolean success = subProjectsFile.delete();
        if (!success) {
            CodeOntology.showWarning("Could not delete subProjects file");
        }
    }

    private void setUp() {
        if (!setUp) {
            loader = new GradleLoader(this);
            buildFile = new File(getPath() + "/" + BuildFiles.GRADLE_FILE);
            loader.handleLocalProperties();
            backup();
            setUp = true;
        }
    }

    protected void backup() {
        String content = getBuildFileContent();
        File buildFile = getBuildFile();
        File backup = new File(buildFile.getPath() + CodeOntology.SUFFIX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backup))) {
            writer.write(content);
        } catch (IOException e) {
            CodeOntology.showWarning("Could not backup build file");
        }
    }

    @Override
    public DependenciesLoader<? extends GradleProject> getLoader() {
        return loader;
    }

    @Override
    public File getBuildFile() {
        return buildFile;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}