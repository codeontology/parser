package org.codeontology.buildsystems.gradle;

import org.apache.commons.io.FileUtils;
import org.codeontology.CodeOntology;
import org.codeontology.buildsystems.DependenciesLoader;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


/**
 * A gradle frontend.
 */
public class GradleLoader extends DependenciesLoader {

    private File gradleLocalRepository;
    private File root;

    public GradleLoader(File root) {
        gradleLocalRepository = new File(System.getProperty("user.home") + "/.gradle");
        this.root = root;
    }

    public GradleLoader(String path) {
        this(new File(path));
    }

    @Override
    public void loadDependencies() {
        GradleModulesHandler modulesHandler = new GradleModulesHandler(root);
        Set<File> modules = modulesHandler.findModules();
        Set<File> subProjects = modulesHandler.findSubProjects();

        if (CodeOntology.getDownloadDependenciesFlag()) {
            downloadDependencies();
        }

        getLoader().loadAllJars(root);
        getLoader().loadAllJars(gradleLocalRepository);

        File src = new File(root.getPath() + "/src/");
        if (src.exists()) {
            getLoader().loadAllJars(src);
        }

        // Run on sub-modules
        for (File module : modules) {
            getLoader().loadAllJars(module);
        }

        // Run on sub-projects: those may be gradle projects as well as maven/ant ones
        // need to get back to the main DefaultManager
        for (File subProject : subProjects) {
            System.out.println("Running on sub-project: " + subProject.getPath());
            getFactory().getLoader(subProject).loadDependencies();
        }
    }

    public void downloadDependencies() {
        try {
            File build = new File(root.getPath() + "/build.gradle");
            Scanner scanner = new Scanner(build);
            Random randomGenerator = new Random();

            String testJarTask = "testJar";
            String taskBody = "(type: Jar) {" + '\n' +
                    '\t' + "classifier = 'tests'" + '\n' +
                    '\t' + "from sourceSets.test.output" + '\n' +
                    "}";

            while (scanner.findInLine("task " + testJarTask + "\\(.+") != null) {
                testJarTask += randomGenerator.nextInt();
            }

            System.out.println("file: " + build.getPath());
            FileUtils.write(build, '\n' + '\n' + "apply plugin: \'java\'" + '\n' + '\n' + "task " + testJarTask + " " + taskBody, true);

            System.out.println("Downloading dependencies...");
            File error = new File(root.getPath() + "/error");
            File output = new File(root.getPath() + "/output");

            ProcessBuilder prB = new ProcessBuilder("gradle", "dependencies");
            prB.directory(root);
            prB.redirectError(error);
            prB.redirectOutput(output);

            prB.start().waitFor();
            System.out.println("Done.");

            System.out.println("Preparing tests with " + testJarTask + "... ");
            prB = new ProcessBuilder("gradle", testJarTask);
            prB.directory(root);
            prB.redirectError(error);
            prB.redirectOutput(output);

            prB.start().waitFor();
            System.out.println("Done.");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
