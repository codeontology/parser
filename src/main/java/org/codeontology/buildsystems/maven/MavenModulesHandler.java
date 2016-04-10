package org.codeontology.buildsystems.maven;


import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * Handle modules.
 */
public class MavenModulesHandler {
    private MavenProject project;


    public MavenModulesHandler(File root) {
        File pom = new File(root.getAbsolutePath() + "/pom.xml");
        this.project = new MavenProject();
        this.project.setFile(pom);
    }

    public void setUp() {
        deleteExtras();
        jarModules();
    }

    /**
     * Get the modules of {@code projectRoot} and run on them
     * too.
     * @return              The set of modules.
     */
    public Set<File> findModules() {
        try {
            Set<File> modules = new HashSet<>();
            File pom = new File(project.getBasedir() + "/pom.xml");
            Scanner scanner = new Scanner(pom);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String match = StringUtils.substringBetween(line, "<module>", "</module>");

                if (!(match == null) && !match.equals("")) {
                    modules.add(new File(project.getBasedir() + "/" + match));
                    System.out.println("Module: " + project.getBasedir() + "/" + match);
                }
            }

            project.getModules().forEach(module -> {
                        System.out.println("module: " + project.getBasedir() + "/" + module);
                        modules.add(new File(project.getBasedir() + "/" + module));
                    }
            );

            return modules;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Create jars for every dependency in {@code projectRoot}.
     * Output folder is the project root, name goes as:
     * {@code dependencyName.jar}.
     */
    public void jarModules() {
        File error = new File(project.getBasedir() + "/error");
        File output = new File(project.getBasedir() + "/output");
        findModules().forEach(module -> {
            if (!module.equals(project.getFile())) {
                System.out.println("Preparing module " + module.getName());
                try {
                    ProcessBuilder prB = new ProcessBuilder("mvn", "jar:jar");
                    prB.directory(module);
                    prB.redirectError(error);
                    prB.redirectOutput(output);

                    prB.start().waitFor();

                    //Runtime.getRuntime().exec("mvn jar:jar", new String[]{}, module).waitFor();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    /**
     * Some maven project allow to exclude some folders from compiling.
     * Let's delete them.
     */
    public void deleteExtras() {
        List<String> modules = project.getModules();
        HashSet<File> folders = new HashSet<>();

        Collections.addAll(folders, project.getBasedir().listFiles(File::isDirectory));
        folders.removeIf(file -> modules.contains(file.getName())
                                || file.getName().equals("src"));
        folders.forEach(File::delete);
    }
}
