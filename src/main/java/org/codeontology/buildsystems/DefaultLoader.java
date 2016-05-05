package org.codeontology.buildsystems;

import java.io.File;

public class DefaultLoader extends DependenciesLoader {

    private File root;

    public DefaultLoader(File root) {
        this.root = root;
    }

    @Override
    public void loadDependencies() {
        if (root.isDirectory()) {
            getLoader().loadAllJars(root);
        }
    }
}
