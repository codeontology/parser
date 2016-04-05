package org.codeontology.frontend.gradle;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codeontology.frontend.DependenciesHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Gradle dependencies handler.
 */
@SuppressWarnings ({ "ResultOfMethodCallIgnored", "SuspiciousMethodCalls" })
public class GradleDependenciesHandler implements DependenciesHandler {

    private static GradleDependenciesHandler instance;
    private static File projectRoot;

    private File downloadDirectory;
    private static Map<File, Set<File>> projectDependenciesMap;


    private GradleDependenciesHandler (File root) {
        projectDependenciesMap = new HashMap<>();
        this.projectRoot = root;
        downloadDirectory = new File(root.getPath() + "/dependencies");
    }


    public static GradleDependenciesHandler getInstance(File root) {
        if (instance == null)
            instance = new GradleDependenciesHandler(root);
        return instance;
    }


    public Set<File> getDependencies () throws Exception {
        try {
            download();
            return projectDependenciesMap.get(projectRoot);
        } catch (InterruptedException e) {
            System.out.println("Dependency download broke");
            throw new Exception("Dependency download broke.");
        }
    }


    public void download () throws IOException, InterruptedException {
        File build = new File(projectRoot.getPath() + "/build.gradle");
        Scanner scanner = new Scanner(build);
        Random randomGenerator = new Random();
        Set<File> dependencies = new HashSet<>();
        String[] options = {""};

        if (!downloadDirectory.exists())
            downloadDirectory.mkdirs();

        downloadDirectory.setWritable(true);

        if (haveDependenciesBeenDownloaded(projectRoot))
            return;

        String testJarTask = "testJar";
        String taskBody = "(type: Jar) {" + '\n' +
                                '\t' + "classifier = 'tests'" + '\n' +
                                '\t' + "from sourceSets.test.output" + '\n' +
                            "}";

        while(scanner.findInLine("task " + testJarTask + "\\(.+") != null)
            testJarTask += randomGenerator.nextInt();

        System.out.println("file: " + build.getPath());
        //FileUtils.write(build, '\n' + '\n' + "apply plugin: \'java\'" + '\n' + '\n' + "task " + testJarTask + " " + taskBody, "UTF-8", true);
        FileUtils.write(build, '\n' + '\n' + "apply plugin: \'java\'" + '\n' + '\n' + "task " + testJarTask + " " + taskBody, true);

        System.out.println("Downloading dependencies in " + downloadDirectory.getPath() + "src/main ");
        File error = new File(projectRoot.getPath() + "/error");
        File output = new File(projectRoot.getPath() + "/output");
        ProcessBuilder prB = new ProcessBuilder("gradle", "dependencies", "-g", downloadDirectory.getName());
        prB.directory(projectRoot);
        prB.redirectError(error);
        prB.redirectOutput(output);

        prB.start().waitFor();
        System.out.println("Done.");

        System.out.println("Preparing tests with " + testJarTask + " ... ");
        ProcessBuilder prB1 = new ProcessBuilder("gradle", testJarTask, "-g ", downloadDirectory.getName());
        prB.directory(projectRoot);
        prB.redirectError(error);
        prB.redirectOutput(output);

        prB.start().waitFor();
        System.out.println("Done.");

        dependencies.addAll(FileUtils.listFiles(downloadDirectory,
                                                FileFilterUtils.suffixFileFilter(".jar"),
                                                TrueFileFilter.INSTANCE));
        dependencies.addAll(FileUtils.listFiles(projectRoot,
                                                FileFilterUtils.suffixFileFilter(".jar"),
                                                TrueFileFilter.INSTANCE));

        projectDependenciesMap.put(projectRoot, dependencies);
    }


    private boolean haveDependenciesBeenDownloaded (File projectRoot) {
        try {
            return projectDependenciesMap.containsKey(projectRoot);
        } catch (NullPointerException e) {
            return false;
        }
    }

}
