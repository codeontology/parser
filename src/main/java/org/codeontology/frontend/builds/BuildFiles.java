package org.codeontology.frontend.builds;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * Standard files for build tools.
 */
public class BuildFiles extends HashMap<File, BuildTool> {

    private static BuildFiles instance;
    private static List<File> files;


    public static  BuildFiles getInstance() {
        if (instance == null)
            instance = new BuildFiles();
        return instance;
    }

    public BuildFiles() {
        super();

        put(Files.antFile, BuildTool.ANT);
        put(Files.gradleFile, BuildTool.GRADLE);
        put(Files.mavenFile, BuildTool.MAVEN);
    }


    public BuildTool get(File root) {

        int gradleBuild = FileUtils.listFiles(root,
                                            FileFilterUtils.nameFileFilter(Files.gradleFile.getName()),
                                            null).size();
        int mavenBuild = FileUtils.listFiles(root,
                                            FileFilterUtils.nameFileFilter(Files.mavenFile.getName()),
                                            null).size();

        if (mavenBuild != 0)
            return BuildTool.MAVEN;
        if (gradleBuild != 0)
            return BuildTool.GRADLE;

        return BuildTool.UNKNOWN;
    }

}

class Files {
    public static final File mavenFile = new File("pom.xml");
    public static final File gradleFile = new File("build.gradle");
    public static final File antFile = new File("build.xml");
}

