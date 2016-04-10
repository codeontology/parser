package org.codeontology.buildsystems.maven;

import org.apache.maven.project.MavenProject;
import org.codeontology.CodeOntology;
import org.codeontology.buildsystems.DependenciesLoader;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class MavenLoader extends DependenciesLoader {
    private static final String PATH_TO_DEPENDENCIES = "/target/dependency/";
    private MavenProject project;

    public MavenLoader(File root) {
        project = new MavenProject();
        File pom = new File(root.getAbsolutePath() + "/pom.xml");
        project.setFile(pom);
    }

    public MavenLoader(String path) {
        this(new File(path));
    }

    @Override
    public void loadDependencies() {
        try {
            MavenModulesHandler modulesHandler = new MavenModulesHandler(project.getBasedir());
            modulesHandler.setUp();
            if (CodeOntology.getDownloadDependenciesFlag()) {
                downloadDependencies();
            }

            getLoader().loadAllJars(project.getBasedir());
            getLoader().loadAllJars(System.getProperty("user.home") + "/.m2");

            Set<File> modules = modulesHandler.findModules();
            for (File module : modules) {
                System.out.println("Running on module " + module.getPath());
                getFactory().getLoader(module).loadDependencies();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get dependencies for maven project in
     * folder {@code projectRoot}, and save them in
     * projectRoot/target/dependency/.
     */
    public void downloadDependencies() {
        try {
            File error = new File(project.getBasedir() + "/error");
            File output = new File(project.getBasedir() + "/output");
            File downloadDirectory = new File(project.getBasedir() + PATH_TO_DEPENDENCIES);

            if (!downloadDirectory.exists()) {
                if (!downloadDirectory.mkdirs()) {
                    throw new IOException("Could not create download directory for dependencies");
                }
            }

            System.out.println("Downloading dependencies...");
            ProcessBuilder prB = new ProcessBuilder("mvn", "dependency:copy-dependencies");
            prB.directory(project.getBasedir());
            prB.redirectError(error);
            prB.redirectOutput(output);

            prB.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
