package org.codeontology.buildsystems;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;

public enum BuildSystem {
    GRADLE,
    MAVEN,
    UNKNOWN;

    public static BuildSystem getBuildSystem(File project) {
        int gradleBuild = FileUtils.listFiles(project,
                FileFilterUtils.nameFileFilter(Files.GRADLE_FILE.getName()),
                null).size();
        int mavenBuild = FileUtils.listFiles(project,
                FileFilterUtils.nameFileFilter(Files.MAVEN_FILE.getName()),
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

class Files {
    public static final File MAVEN_FILE = new File("pom.xml");
    public static final File GRADLE_FILE = new File("build.gradle");
}
