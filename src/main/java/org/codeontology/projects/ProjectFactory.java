package org.codeontology.projects;

import org.codeontology.projects.gradle.AndroidProject;
import org.codeontology.projects.gradle.GradleProject;
import org.codeontology.projects.maven.MavenProject;

import java.io.File;

public class ProjectFactory {
    private static ProjectFactory instance;

    private ProjectFactory() {

    }

    public static ProjectFactory getInstance() {
        if (instance == null) {
            instance = new ProjectFactory();
        }

        return instance;
    }

    public Project getProject(String path) {
        return getProject(new File(path));
    }

    public Project getProject(File project) {
        switch (BuildSystem.getBuildSystem(project)) {
            case MAVEN:
                return new MavenProject(project);
            case GRADLE:
                if (new File(project.getPath() + "/src/main/AndroidManifest.xml").exists()) {
                    return new AndroidProject(project);
                } else {
                    return new GradleProject(project);
                }
        }

        return new DefaultProject(project);
    }
}
