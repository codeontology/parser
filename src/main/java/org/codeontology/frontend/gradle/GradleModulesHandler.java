package org.codeontology.frontend.gradle;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Handle modules.
 */
public class GradleModulesHandler {

    private static GradleModulesHandler instance;

    private static File projectRoot;

    private static final String MODULES_SETTINGS = "/settings.gradle";


    public GradleModulesHandler (File project) {
        projectRoot = project;
    }


    public static GradleModulesHandler getInstance(File projectRoot) {
        if (instance == null)
            instance = new GradleModulesHandler(projectRoot);
        return instance;
    }


    public static void setRoot(File root) {
        if (instance == null)
            getInstance(root);
        else
            projectRoot = root;
    }


    /**
     * Get the sub-projects of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public static Set<File> findSubProjects() throws FileNotFoundException {
        System.out.println("Searching subprojects for " + projectRoot.getPath());
        Set<File> subProjects= new HashSet<>();
        File settings = new File(projectRoot.getPath() + "/settings.gradle");
        Scanner scanner = new Scanner(settings);

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.startsWith("include ")) {
                String module = line.split(" ")[1]
                                        .replaceAll(":","/");

            subProjects.add(new File(projectRoot.getPath() + "/" + module.substring(1,module.length() - 1)));
            }
        }

        return subProjects;
    }


    /**
     * Get the modules of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public static Set<File> findModules() {
        File modulesFolder = new File(projectRoot.getPath() + "/modules");
        Set<File> modules = new HashSet<>();

        if (modulesFolder.exists())
            Collections.addAll(modules, modulesFolder.listFiles(File::isDirectory));

        return modules;
    }


    /**
     * Create jars for every dependency in {@code projectRoot}.
     * Output folder is the project root, name goes as:
     * {@code dependencyName.jar}.
     * @return  The set of jars modules.
     */
    public static Set<File> jarModules() throws IOException,
                                                InterruptedException {
        File error = new File(projectRoot.getPath() + "/error");
        File output = new File(projectRoot.getPath() + "/output");
        File downloadDirectory = new File(projectRoot + "/dependencies/");
        Set<File> jars = new HashSet<>();
        System.out.println("Preparing modules.. ");
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

        if (!plugin)
            (new FileWriter(build, true)).append("apply plugin: \'java\'");

        ProcessBuilder prB = new ProcessBuilder("gradle", "jar");
        prB.directory(projectRoot);
        prB.redirectError(error);
        prB.redirectOutput(output);

        prB.start().waitFor();
        System.out.println("Done.");

        if (!downloadDirectory.exists())
            downloadDirectory.mkdir();

        jars.addAll(FileUtils.listFiles(downloadDirectory,
                FileFilterUtils.suffixFileFilter(".jar"),
                TrueFileFilter.INSTANCE));

        return jars;
    }


    /**
     * Some maven project allow to exclude some folders from compiling.
     * Let's delete them.
     * @param project   The project.
     */
    public static void deleteExtras(File project) throws Exception {
        HashSet<File> folders = new HashSet<>();
        Set<File> subprojects = findSubProjects();

        Collections.addAll(folders, project.listFiles(File::isDirectory));
        folders.removeIf(file -> subprojects.contains(file)
                || file.getName().equals("src")
                || file.getName().equals("modules"));
        folders.forEach(File::delete);
    }
}
