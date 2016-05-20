package org.codeontology.extraction;

import org.codeontology.CodeOntology;
import org.codeontology.ProjectProcessor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class SourceProcessor extends AbstractProcessor<CtPackage> {
    @Override
    public void process(CtPackage pack) {
        ReflectionFactory.getInstance().setParent(pack.getFactory());
        PackageEntity packageEntity = EntityFactory.getInstance().wrap(pack);
        if (CodeOntology.extractProjectStructure()) {
            ProjectEntity<?> project = new ProjectProcessor(CodeOntology.getProject()).getProjectEntity();
            packageEntity.setParent(project);
        }
        packageEntity.extract();
    }
}
