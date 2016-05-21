package org.codeontology.projects.gradle;

import org.codeontology.extraction.ProjectVisitor;
import org.codeontology.projects.BuildFiles;
import org.codeontology.projects.DependenciesLoader;
import org.codeontology.projects.Project;

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
    private boolean setUp = false;

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
                "\t\ttask -> new File(rootDir, \"subProjects\").append(\"$task.project.projectDir\\n\");\n" +
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

            try (Scanner scanner = new Scanner(new File(getPath() + "/subProjects"))) {
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
            System.out.println("No subprojects found for project " + getProjectDirectory().getName() + ".");
        }

        return initSubProjects(subProjects);
    }

    private void setUp() {
        if (!setUp) {
            loader = new GradleLoader(this);
            buildFile = new File(getPath() + "/" + BuildFiles.GRADLE_FILE);
            loader.handleLocalProperties();
            setUp = true;
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
