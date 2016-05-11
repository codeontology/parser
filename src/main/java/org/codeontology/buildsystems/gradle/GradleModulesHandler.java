package org.codeontology.buildsystems.gradle;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codeontology.CodeOntology;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Handle modules.
 */
public class GradleModulesHandler {

    private GradleLoader loader;
    private File projectRoot;


    public GradleModulesHandler(GradleLoader loader) {
        this.loader = loader;
        projectRoot = loader.getProjectDirectory();
    }

    public void setUp() {
        jarModules();
    }

    /**
     * Get the sub-projects of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public Set<File> findSubProjects() {
        Set<File> subProjects = new HashSet<>();
        String task = "subprojects {\n" +
                "\ttask CodeOntologySub << {\n" +
                "\t\ttask -> new File(rootDir, \"subProjects\").append(\"$task.project.projectDir\\n\");\n" +
                "\t}\n" +
                "}";
        File buildFile = loader.getBuildFile();
        String content = loader.getBuildFileContent();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(buildFile, true))) {
            if (!content.contains(task)) {
                writer.write("\n\n" + task);
                writer.close();
            }

            loader.runTask("CodeOntologySub");

            try (Scanner scanner = new Scanner(new File(loader.getProjectDirectory().getPath() + "/subProjects"))) {
                while (scanner.hasNextLine()) {
                    subProjects.add(new File(scanner.nextLine()));
                }
            }

        } catch (IOException e) {
            CodeOntology.showWarning("Could not get subprojects");
        }

        return subProjects;
    }

    /**
     * Get the modules of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public Set<File> findModules() {
        File modulesFolder = new File(projectRoot.getPath() + "/modules");
        Set<File> modules = new HashSet<>();

        if (modulesFolder.exists()) {
            Collections.addAll(modules, modulesFolder.listFiles(File::isDirectory));
        }
        return modules;
    }

    /**
     * Create jars for every dependency in {@code projectRoot}.
     * Output folder is the project root, name goes as:
     * {@code dependencyName.jar}.
     * @return  The set of jars modules.
     */
    public Set<File> jarModules() {
        try {
            Set<File> jars = new HashSet<>();
            File build = new File(projectRoot.getPath() + "/build.gradle");

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
                writer.append("apply plugin: \'java\'");
                writer.close();
            }

            loader.getBuilder("jar").start().waitFor();

            jars.addAll(FileUtils.listFiles(projectRoot,
                    FileFilterUtils.suffixFileFilter(".jar"),
                    TrueFileFilter.INSTANCE));

            return jars;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
