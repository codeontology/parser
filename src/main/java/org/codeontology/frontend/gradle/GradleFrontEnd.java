package org.codeontology.frontend.gradle;


import org.codeontology.frontend.FrontEnd;
import org.codeontology.frontend.JPPFrontEnd;
import org.codeontology.frontend.builds.AnalysisMap;
import org.codeontology.frontend.builds.ExplorationStatus;

import java.io.File;
import java.util.Set;


/**
 * A gradle frontend.
 */
public class GradleFrontEnd extends JPPFrontEnd implements FrontEnd {

    private static GradleFrontEnd instance;
    private static File gradleLocalRepository;

    private static boolean getJars = true;


    private GradleFrontEnd() {
        gradleLocalRepository = new File(System.getProperty("user.home") + "/.gradle");
    }


    public static GradleFrontEnd getInstance() {
        if (instance == null)
            instance = new GradleFrontEnd();
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void run (File root, File outputFile, boolean downloadDependencies) throws Exception {
        if (AnalysisMap.getInstance().get(root) == ExplorationStatus.EXPLORED)
            return;
        if (getStandardClasspath() == null)
            setStandardClasspath(root);

        AnalysisMap.getInstance().put(root, ExplorationStatus.EXPLORING);
        GradleModulesHandler.setRoot(root);

        GradleModulesHandler.deleteExtras(root);
        GradleModulesHandler.jarModules();
        GradleDependenciesHandler.getInstance(root).download();

        Set<File> modules = GradleModulesHandler.findModules();
        Set<File> subProjects = GradleModulesHandler.findSubProjects();
        File src = new File(root.getPath() + "/src/");

        if (src.exists()) {
            System.out.println("Running on source: " + root.getPath() + "/src/ ");
            runScript(root, outputFile, getJars, root, gradleLocalRepository);
            getJars = false;
        }


        // Run on sub-modules
        modules.forEach(module -> {
            try {
                System.out.println("Running on module " + module.getPath());
                runScript(module, outputFile, getJars, root, gradleLocalRepository);
                if (!getJars)
                    getJars = false;
            }
            catch (Exception e) {
                // Error, abort
                outputFile.deleteOnExit();
                System.exit(- 1);
            }
        });


        // Run on sub-projects: those may be gradle projects as well as maven/ant ones
        // need to get back to the main FrontEnd
        subProjects.forEach(subProject -> {
            try {
                System.out.println("Running on sub-project: " + subProject.getPath());
                runScript(subProject, outputFile, getJars, root, gradleLocalRepository);
                AnalysisMap.getInstance().put(root, ExplorationStatus.EXPLORED);
            } catch (Exception e) {
                // Error, abort
                outputFile.deleteOnExit();
                System.exit(- 1);
            }
        });

    }

}
