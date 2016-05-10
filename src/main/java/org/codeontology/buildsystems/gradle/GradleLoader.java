package org.codeontology.buildsystems.gradle;

import org.apache.commons.io.FileUtils;
import org.codeontology.CodeOntology;
import org.codeontology.buildsystems.DependenciesLoader;

import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * A gradle frontend.
 */
public class GradleLoader extends DependenciesLoader {

    private File gradleLocalRepository;
    private File root;
    private File buildFile;
    private File error;
    private File output;

    public GradleLoader(File root) {
        gradleLocalRepository = new File(System.getProperty("user.home") + "/.gradle");
        this.root = root;
        buildFile = new File(root.getPath() + "/build.gradle");
        error = new File(root.getPath() + "/error");
        output = new File(root.getPath() + "/output");
    }

    @Override
    public void loadDependencies() {
        System.out.println("Loading dependencies with gradle...");
        removeLocalProperties();
        GradleModulesHandler modulesHandler = new GradleModulesHandler(root);
        Set<File> modules = modulesHandler.findModules();
        Set<File> subProjects = modulesHandler.findSubProjects();

        if (CodeOntology.downloadDependencies()) {
            downloadDependencies();
        }

        runTasks();

        loadClasspath();

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

    private void removeLocalProperties() {
        File localProperties = new File(getRoot().getPath() + "/local.properties");
        if (localProperties.exists()) {
            try {
                FileUtils.forceDelete(localProperties);
            } catch (IOException e) {
                CodeOntology.showWarning("Could not delete local properties.");
            }
        }
    }

    protected void loadClasspath() {
        File classpathFile = new File(root + "/build/cp");
        String classpath = "";

        try (Scanner scanner = new Scanner(classpathFile)) {
            if (scanner.hasNext()) {
                classpath = scanner.next().trim();
            }
            if (classpath.equals("")) {
                loadAllAvailableJars();
            } else {
                getLoader().loadClasspath(classpath);
            }
        } catch (FileNotFoundException e) {
            loadAllAvailableJars();
        }

        File src = new File(root.getPath() + "/src/");
        if (src.exists()) {
            getLoader().loadAllJars(src);
        }
    }

    protected void loadAllAvailableJars() {
        getLoader().loadAllJars(root);
        getLoader().lock();
        getLoader().loadAllJars(gradleLocalRepository);
        getLoader().release();
    }

    public void downloadDependencies() {
        try {
            System.out.println("Downloading dependencies...");
            ProcessBuilder builder = new ProcessBuilder("gradle", "dependencies");
            builder.directory(root);
            builder.redirectError(error);
            builder.redirectOutput(output);
            builder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runTasks() {
        applyPlugin("java");

        String testJarTask = "CodeOntologyTestJar";
        String testJarTaskBody = "(type: Jar) {" + '\n' +
                '\t' + "classifier = 'tests'" + '\n' +
                '\t' + "from sourceSets.test.output" + '\n' +
                "}";

        addTask(testJarTask, testJarTaskBody);
        runTask(testJarTask);

        String separator = System.getProperty("line.separator");
        String cpFileTask = "CodeOntologyCpFile";
        String cpFileTaskBody = "{" + separator +
                '\t' + "buildDir.mkdirs()" + separator +
                '\t' + "new File(buildDir, \"cp\").text = configurations.runtime.asPath" + separator +
                "}";

        addTask(cpFileTask, cpFileTaskBody);
        runTask(cpFileTask);
    }

    private void applyPlugin(String plugin) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(buildFile, true)))) {
            String build = getBuildFileContent();
            String applyPlugin = "apply plugin: " + "\'" + plugin + "\'";
            if (build != null && !build.contains(applyPlugin)) {
                writer.write("\n" + " \n" + applyPlugin);
            }
        } catch (IOException e) {
            System.out.println("Could not apply plugin " + plugin);
        }
    }

    protected void addTask(String name, String body) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(buildFile, true)))) {
            String build = getBuildFileContent();
            Pattern pattern = Pattern.compile(".*task\\s+" + name + ".*", Pattern.DOTALL);
            if (build != null && !pattern.matcher(build).matches()) {
                writer.write("\n" + "\n" + "task " + name + " " + body);
            }
        } catch (IOException e) {
            CodeOntology.showWarning("Could not add task " + name);
        }
    }

    protected void runTask(String name) {
        try {
            ProcessBuilder builder = new ProcessBuilder("gradle", name);
            builder.directory(root);
            builder.redirectError(error);
            builder.redirectOutput(output);
            builder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not run task " + name);
        }
    }

    private String getBuildFileContent() {
        try (Scanner scanner = new Scanner(buildFile)) {
            scanner.useDelimiter("\\Z");
            String build = "";
            if (scanner.hasNext()) {
                build = scanner.next();
            }
            return build;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public File getRoot() {
        return root;
    }

    public File getError() {
        return error;
    }

    public File getOutput() {
        return output;
    }
}
