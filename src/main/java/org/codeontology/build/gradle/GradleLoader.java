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

package org.codeontology.build.gradle;

import org.apache.commons.io.FileUtils;
import org.codeontology.CodeOntology;
import org.codeontology.build.DependenciesLoader;
import org.codeontology.build.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;


public class GradleLoader extends DependenciesLoader<GradleProject> {

    private File gradleLocalRepository;
    private File error;
    private File output;
    public static final String CLASSPATH_FILE_NAME = "cp" + CodeOntology.SUFFIX;
    private boolean localPropertiesHandled;

    public GradleLoader(GradleProject project) {
        super(project);
        gradleLocalRepository = new File(System.getProperty("user.home") + "/.gradle");
        error = new File(project.getProjectDirectory().getPath() + "/error");
        output = new File(project.getProjectDirectory().getPath() + "/output");
        localPropertiesHandled = false;
    }

    @Override
    public void loadDependencies() {
        System.out.println("Loading dependencies with gradle...");

        handleLocalProperties();

        if (CodeOntology.downloadDependencies()) {
            downloadDependencies();
        }

        jarProjects();
        runTasks();
        loadClasspath();
        runOnSubProjects();
        removeClassPathFiles();
    }

    private void runOnSubProjects() {
        Collection<Project> subProjects = getProject().getSubProjects();
        for (Project subProject : subProjects) {
            System.out.println("Running on subproject: " + subProject.getProjectDirectory().getPath());
            DependenciesLoader<?> loader = subProject.getLoader();
            loader.loadDependencies();
        }
    }

    public void handleLocalProperties() {
        if (localPropertiesHandled) {
            return;
        }

        localPropertiesHandled = true;
        File localProperties = new File(getProject().getPath() + "/local.properties");
        File tmp = new File(getProject().getPath() + "/.tmp.properties");
        if (!localProperties.exists()) {
            return;
        }
        try (Scanner scanner = new Scanner(localProperties)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmp))) {

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.trim().startsWith("sdk.dir")) {
                        writer.write(line + "\n");
                    }
                }

                boolean success = localProperties.renameTo(new File(localProperties.getPath() + CodeOntology.SUFFIX));
                success = success && tmp.renameTo(localProperties);
                if (!success) {
                    FileUtils.forceDelete(localProperties);
                    FileUtils.forceDelete(tmp);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadClasspath() {
        File classpathFile = new File(getProject().getPath() + "/build/" + CLASSPATH_FILE_NAME);
        String classpath = "";

        try (Scanner scanner = new Scanner(classpathFile)) {
            scanner.useDelimiter("\\Z");
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

        File src = new File(getProject().getPath() + "/src/");
        if (src.exists()) {
            getLoader().loadAllJars(src);
        }
    }

    private void removeClassPathFiles() {
        String pathString = getProject().getPath();
        try {
            Files.walk(Paths.get(pathString))
                    .filter(path -> path.toFile().getName().equals(CLASSPATH_FILE_NAME))
                    .forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            CodeOntology.showWarning("Could not remove classpath files");
        }
    }

    protected void loadAllAvailableJars() {
        getLoader().loadAllJars(getProject().getRoot());
        getLoader().lock();
        getLoader().loadAllJars(gradleLocalRepository);
        getLoader().release();
    }

    public void downloadDependencies() {
        try {
            System.out.println("Downloading dependencies...");
            getProcessBuilder("dependencies").start().waitFor();
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
                '\t' + "new File(buildDir, \"" + CLASSPATH_FILE_NAME + "\").text = configurations.runtime.asPath" + separator +
                "}";

        addTask(cpFileTask, cpFileTaskBody);
        runTask(cpFileTask);
    }

    protected void applyPlugin(String plugin) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getProject().getBuildFile(), true)))) {
            String build = getProject().getBuildFileContent();
            String applyPlugin = "apply plugin: " + "\'" + plugin + "\'";
            if (build != null && !hasPlugin(plugin)) {
                writer.write("\n" + " \n" + applyPlugin);
            }
        } catch (IOException e) {
            CodeOntology.showWarning("Could not apply plugin " + plugin);
        }
    }

    public boolean hasPlugin(String plugin) {
        String buildFile = getProject().getBuildFileContent();
        String regex = ".*apply\\s+plugin\\s*:\\s+\'" + plugin + "\'.*";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return pattern.matcher(buildFile).matches();
    }

    protected void addTask(String name, String body) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getProject().getBuildFile(), true)))) {
            String build = getProject().getBuildFileContent();
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
            getProcessBuilder(name).start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not run task " + name);
        }
    }

    public ProcessBuilder getProcessBuilder(String command) {
        ProcessBuilder builder;
        File gradlew = new File(getProject().getRoot().getPath() + "/gradlew");
        if (gradlew.exists()) {
            if (!gradlew.setExecutable(true)) {
                CodeOntology.showWarning("Could not execute gradlew");
            }
            builder = new ProcessBuilder("bash", "-c", "./gradlew " + command);
            builder.directory(getProject().getRoot());
        } else {
            builder = new ProcessBuilder("gradle", command);
            builder.directory(getProject().getProjectDirectory());
        }

        builder.redirectError(error);
        builder.redirectOutput(output);

        return builder;
    }

    public void jarProjects() {
        try {
            File build = new File(getProject().getRoot().getPath() + "/build.gradle");

            Scanner scanner = new Scanner(build);
            boolean plugin = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.contains("apply plugin: \'java\'")) {
                    plugin = true;
                    break;
                }
            }

            if (!plugin) {
                FileWriter writer = new FileWriter(build, true);
                writer.append("\n\napply plugin: \'java\'");
                writer.close();
            }

            getProcessBuilder("jar").start().waitFor();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}