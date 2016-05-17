package org.codeontology.extraction;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class SourceProcessor extends AbstractProcessor<CtPackage> {
    @Override
    public void process(CtPackage pack) {
        ReflectionFactory.getInstance().setParent(pack.getFactory());
        EntityFactory.getInstance().wrap(pack).extract();
    }
}
