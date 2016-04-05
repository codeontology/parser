package org.codeontology.frontend.maven;


import org.apache.commons.lang3.StringUtils;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.*;


/**
 * Handle modules.
 */
public class MavenModulesHandler {

    private static MavenModulesHandler instance;

    private static File projectRoot;


    public MavenModulesHandler (File project) {
        projectRoot = project;
    }

    public static MavenModulesHandler getInstance(File projectRoot) {
        if (instance == null)
            instance = new MavenModulesHandler(projectRoot);
        return instance;
    }


    public static void setRoot(File projectRoot) {
        if (instance == null)
            getInstance(projectRoot);
        else
            MavenModulesHandler.projectRoot = projectRoot;
    }


    /**
     * Get the modules of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public static Set<File> findModules() throws Exception {
        Set<File> modules = new HashSet<>();
        MavenProject mavenProject = MavenFrontEnd.getProject(projectRoot);
        File pom = new File(mavenProject.getBasedir() + "/pom.xml");
        Scanner scanner = new Scanner(pom);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String match = StringUtils.substringBetween(line, "<module>", "</module>");

            if (!(match == null) && !match.equals("")) {
                modules.add(new File(projectRoot.getPath() + "/" + match));
                System.out.println("Module: " + projectRoot.getPath() + "/" + match);
            }
        }

        mavenProject.getModules().forEach(module -> {
            System.out.println("module: " + projectRoot.getPath() + "/" + module);
            modules.add(new File(projectRoot.getPath() + "/" + module));
            }
        );

        return modules;
    }


    /**
     * Create jars for every dependency in {@code projectRoot}.
     * Output folder is the project root, name goes as:
     * {@code dependencyName.jar}.
     */
    public static void jarModules() throws Exception {
        File error = new File(projectRoot.getPath() + "/error");
        File output = new File(projectRoot.getPath() + "/output");
        findModules().forEach(module -> {
            if (!module.equals(projectRoot)) {
                System.out.println("Preparing module " + module.getName());
                try {
                    ProcessBuilder prB = new ProcessBuilder("mvn", "jar:jar");
                    prB.directory(module);
                    prB.redirectError(error);
                    prB.redirectOutput(output);

                    prB.start().waitFor();

                    //Runtime.getRuntime().exec("mvn jar:jar", new String[]{}, module).waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        });
    }


    /**
     * Some maven project allow to exclude some folders from compiling.
     * Let's delete them.
     * @param project   The project.
     */
    public static void deleteExtras(MavenProject project) throws Exception {
        List<String> modules = project.getModules();
        HashSet<File> folders = new HashSet<>();

        Collections.addAll(folders, project.getBasedir().listFiles(File::isDirectory));
        folders.removeIf(file -> modules.contains(file.getName())
                                || file.getName().equals("src"));
        folders.forEach(File::delete);
    }
}
