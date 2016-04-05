package org.codeontology.frontend;


import java.io.File;


/**
 * Frontend management for build tools.
 */
public interface FrontEnd {

    /**
     * Run the jpp on the given {@code root}.
     * @param root                      The project root.
     * @param outputFile                The output file for the project.
     * @param downloadDependencies      True if dependencies should be downloaded, false otherwise.
     */
    void run(File root, File outputFile, boolean downloadDependencies) throws Exception;

}
