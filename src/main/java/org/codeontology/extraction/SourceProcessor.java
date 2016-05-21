package org.codeontology.extraction;

import org.codeontology.CodeOntology;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class SourceProcessor extends AbstractProcessor<CtPackage> {
    @Override
    public void process(CtPackage pack) {
        ReflectionFactory.getInstance().setParent(pack.getFactory());
        PackageEntity packageEntity = EntityFactory.getInstance().wrap(pack);
        if (CodeOntology.extractProjectStructure()) {
            packageEntity.setParent(CodeOntology.getProject());
        }
        packageEntity.extract();
    }
}
