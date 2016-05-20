package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.buildsystems.gradle.AndroidProject;

public class AndroidGradleProjectEntity extends GradleProjectEntity {
    public AndroidGradleProjectEntity(AndroidProject project) {
        super(project);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ANDROID_GRADLE_PROJECT_ENTITY;
    }
}
