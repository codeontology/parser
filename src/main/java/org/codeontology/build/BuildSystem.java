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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;

public enum BuildSystem {
    GRADLE,
    MAVEN,
    UNKNOWN;

    public static BuildSystem getBuildSystem(File project) {
        if (!project.isDirectory()) {
            return UNKNOWN;
        }

        int gradleBuild = FileUtils.listFiles(project,
                FileFilterUtils.nameFileFilter(BuildFiles.GRADLE_FILE.getName()),
                null).size();
        int mavenBuild = FileUtils.listFiles(project,
                FileFilterUtils.nameFileFilter(BuildFiles.MAVEN_FILE.getName()),
                null).size();

        if (mavenBuild != 0) {
            return MAVEN;
        }
        if (gradleBuild != 0) {
            return GRADLE;
        }

        return UNKNOWN;
    }
}