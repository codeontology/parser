package org.codeontology.frontend.maven;


import org.apache.maven.project.MavenProject;
import org.codeontology.frontend.FrontEnd;
import org.codeontology.frontend.JPPFrontEnd;
import org.codeontology.frontend.builds.BuildTool;


import java.io.File;


/**
 * A maven frontend.
 */
public class MavenFrontEnd extends JPPFrontEnd implements FrontEnd {

    private static MavenFrontEnd instance;

    private static File mavenLocalRepository;
    private static boolean getJars = true;


    private MavenFrontEnd() {
        mavenLocalRepository= new File(System.getProperty("user.home") + "/.m2/repository/");
    }


    public static MavenFrontEnd getInstance() {
        if (instance == null)
            instance = new MavenFrontEnd();
        return instance;
    }

    @Override
    public void run (File root, File outputFile, boolean downloadDependencies) throws Exception {
        MavenModulesHandler.setRoot(root);
        if (downloadDependencies)
            MavenDependenciesHandler.getInstance().download(root);
        MavenProject mavenProject = getProject(root);

        MavenModulesHandler.deleteExtras(mavenProject);
        MavenModulesHandler.jarModules();

        if ((new File(root.getPath() + "/src/main").exists())) {
            System.out.println("Running on source: " + root.getPath() + "/src/main ");
            runScript(new File(root.getPath() + "/src/main"), outputFile, getJars, mavenLocalRepository, root);
            getJars = false;
        }

 	if ((new File(root.getPath() + "/src/test").exists())) {
            System.out.println("Running on test: " + root.getPath() + "/src/test ");
            runScript(new File(root.getPath() + "/src/test"), outputFile, getJars, mavenLocalRepository, root);
            getJars = false;
        }

        MavenModulesHandler.findModules().forEach(module -> {
            try {
                System.out.println("Running on module " + module.getPath());
                if (BuildTool.is(module) == BuildTool.MAVEN)
                    run(module, outputFile, downloadDependencies);
                else
                    runScript(module, outputFile, getJars, root, mavenLocalRepository);
                getJars = false;
            } catch (Exception e) {
                // Error, abort
                outputFile.deleteOnExit();
                System.exit(- 1);
            }
        });
    }


    /**
     * Create a {@link MavenProject} in the given {@code rootDir}.
     * @param rootDir   The project root directory.
     * @return          The maven project in {@code rootDir}.
     */
    public static MavenProject getProject(File rootDir) {
        MavenProject project = new MavenProject();
        File pom = new File(rootDir.getAbsolutePath() + "/pom.xml");

        //project.setPomFile(pom);
        project.setFile(pom);

        return project;
    }

}
