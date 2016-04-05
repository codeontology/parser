package org.codeontology.frontend.StandardFrontEnd;

import org.codeontology.frontend.FrontEnd;
import org.codeontology.frontend.JPPFrontEnd;

import java.io.File;


/**
 * Frontend for projects with no build tools.
 */
public class StandardFrontEnd extends JPPFrontEnd implements FrontEnd {

    private static StandardFrontEnd instance;

    private static Runtime env = Runtime.getRuntime();

    public static StandardFrontEnd getInstance () {
        if (instance == null)
            instance = new StandardFrontEnd();
        return instance;
    }


    public void run (File root, File classpath, File outputFile, boolean downloadDependencies) throws Exception {

        System.out.println("[STD] Running on source: " + root.getPath());
        runScript(root, outputFile, true, root);

    }

    @Override
    public void run (File root, File outputFile, boolean downloadDependencies) throws Exception {
        throw new Exception("Need classpath");
    }
}
