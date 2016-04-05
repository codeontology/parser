package org.codeontology.frontend;


import org.apache.maven.project.ProjectBuildingException;

import java.io.File;
import java.io.IOException;
import java.util.Set;


/**
 * Handle project dependencies.
 */
public interface DependenciesHandler {

    /**
     * Get the dependencies for {@code projectRoot}.
     * @return                  The set of dependencies.
     */
    Set<File> getDependencies() throws Exception;


    /**
     * Get dependencies for maven project in
     * folder {@code projectRoot}, and save them in
     * projectRoot/target/dependency/.
     */
    void download () throws IOException,
                            InterruptedException, ProjectBuildingException;

}
