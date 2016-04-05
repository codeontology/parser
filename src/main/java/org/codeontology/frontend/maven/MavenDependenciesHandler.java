package org.codeontology.frontend.maven;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.apache.maven.project.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Maven dependencies handler.
 */
public class MavenDependenciesHandler {

    private static MavenDependenciesHandler instance;

    private static final String PATH_TO_DEPENDENCIES = "/target/dependency/";
    private static Map<File, Set<File>> projectDependenciesMap;


    private MavenDependenciesHandler () {
        projectDependenciesMap = new HashMap<>();
    }


    public static MavenDependenciesHandler getInstance() {
        if (instance == null)
            instance = new MavenDependenciesHandler();
        return instance;
    }


    public Set<File> getDependencies (File projectRoot) throws Exception {
        return getDependencies(MavenFrontEnd.getProject(projectRoot));
    }

    public void download (File projectRoot) throws IOException,
                                                    InterruptedException,
                                                    ProjectBuildingException {
        download(MavenFrontEnd.getProject(projectRoot));
    }

    public boolean haveDependenciesBeenDownloaded (File projectRoot) throws Exception {
        return haveDependenciesBeenDownloaded(MavenFrontEnd.getProject(projectRoot));
    }


    private Set<File> getDependencies(MavenProject project) throws Exception {
        try {
            download(project);
            return projectDependenciesMap.get(project.getBasedir());
        } catch (InterruptedException e) {
            throw new Exception("Dependency download broke.");
        }
    }


    /**
     * Get dependencies for maven project in
     * folder {@code projectRoot}, and save them in
     * projectRoot/target/dependency/.
     * @param project       The project.
     */
    private void download (MavenProject project) throws IOException,
                                                        InterruptedException {
        try {
            File error = new File(project.getBasedir() + "/error");
            File output = new File(project.getBasedir() + "/output");
            File downloadDirectory = new File(project.getBasedir() + PATH_TO_DEPENDENCIES);
            Set<File> dependencies = new HashSet<>();

            if (projectDependenciesMap.containsKey(project.getBasedir()))
                return;
            if (!downloadDirectory.exists())
                //noinspection ResultOfMethodCallIgnored
                downloadDirectory.mkdirs();

            System.out.println("Downloading dependencies in " + downloadDirectory.getPath() + "... ");
            ProcessBuilder prB = new ProcessBuilder("mvn", "dependency:copy-dependencies");
            prB.directory(project.getBasedir());
            prB.redirectError(error);
            prB.redirectOutput(output);

            prB.start().waitFor();

            dependencies.addAll(FileUtils.listFiles(downloadDirectory,
                    FileFilterUtils.suffixFileFilter(".jar"),
                    TrueFileFilter.INSTANCE));
            // Parent depending on children modules
            dependencies.addAll(FileUtils.listFiles(project.getBasedir(),
                    FileFilterUtils.suffixFileFilter(".jar"),
                    TrueFileFilter.INSTANCE));
            // Basic dependencies
            dependencies.addAll(FileUtils.listFiles(new File(System.getProperty("user.home") + "/.m2"),
                    FileFilterUtils.suffixFileFilter(".jar"),
                    TrueFileFilter.INSTANCE));

            projectDependenciesMap.put(project.getBasedir(), dependencies);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
        }
    }


    private boolean haveDependenciesBeenDownloaded (MavenProject project) {
        return projectDependenciesMap.containsKey(project.getBasedir());
    }

}
