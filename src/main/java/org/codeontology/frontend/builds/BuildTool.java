package org.codeontology.frontend.builds;


import java.io.File;


/**
 * Build tools supported.
 */
public enum BuildTool {
    MAVEN,
    GRADLE,
    ANT,
    UNKNOWN;

    private static BuildFiles map = BuildFiles.getInstance();

    /**
     * Returns the build tool of {@code root}.
     * @param root  The project root.
     * @return      The project build tool.
     */
    public static BuildTool is(File root) {
        return map.get(root);
    }
}
