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

import org.codeontology.build.gradle.AndroidProject;
import org.codeontology.build.gradle.GradleProject;
import org.codeontology.build.maven.MavenProject;

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