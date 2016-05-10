package org.codeontology.buildsystems.maven;

import org.apache.maven.project.MavenProject;
import org.codeontology.CodeOntology;
import org.codeontology.buildsystems.ClasspathLoader;
import org.codeontology.buildsystems.DependenciesLoader;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class MavenLoader extends DependenciesLoader {
    private static final String PATH_TO_DEPENDENCIES = "/target/dependency/";
    private final File output;
    private final File error;
    private MavenProject project;
    private static boolean m2Loaded = false;

    public MavenLoader(File root) {
        project = new MavenProject();
        File pom = new File(root.getAbsolutePath() + "/pom.xml");
        project.setFile(pom);
        error = new File(project.getBasedir() + "/error");
        output = new File(project.getBasedir() + "/output");
    }

    @Override
    public void loadDependencies() {
        System.out.println("Loading dependencies with Maven");
        try {
            MavenModulesHandler modulesHandler = new MavenModulesHandler(project.getBasedir());
            if (CodeOntology.downloadDependencies()) {
                modulesHandler.setUp();
                downloadDependencies();
            }

            ProcessBuilder builder = new ProcessBuilder("mvn", "dependency:build-classpath", "-Dmdep.outputFile=.cp");
            builder.directory(project.getBasedir());
            builder.redirectError(error);
            builder.redirectOutput(output);
            int exitStatus = builder.start().waitFor();

            if (exitStatus == 0) {
                File classpath = new File(project.getBasedir() + "/.cp");
                Scanner reader = new Scanner(classpath);
                reader.useDelimiter("\\Z");
                if (reader.hasNext()) {
                    getLoader().loadClasspath(reader.next());
                }
                reader.close();
                classpath.deleteOnExit();
            } else {
                getLoader().loadAllJars(project.getBasedir());
                if (!m2Loaded) {
                    ClasspathLoader loader = getLoader();
                    loader.lock();
                    loader.loadAllJars(System.getProperty("user.home") + "/.m2");
                    loader.release();
                    m2Loaded = true;
                }
            }


            Set<File> modules = modulesHandler.findModules();
            for (File module : modules) {
                System.out.println("Running on module " + module.getPath());
                getFactory().getLoader(module).loadDependencies();
            }
        } catch (IOException | InterruptedException e) {
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
            File downloadDirectory = new File(project.getBasedir() + PATH_TO_DEPENDENCIES);

            if (!downloadDirectory.exists()) {
                if (!downloadDirectory.mkdirs()) {
                    throw new IOException("Could not create download directory for dependencies");
                }
            }

            System.out.println("Downloading dependencies...");
            ProcessBuilder builder = new ProcessBuilder("mvn", "dependency:copy-dependencies");
            builder.directory(project.getBasedir());
            builder.redirectError(error);
            builder.redirectOutput(output);

            builder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
