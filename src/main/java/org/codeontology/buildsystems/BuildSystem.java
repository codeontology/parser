package org.codeontology.buildsystems;

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
