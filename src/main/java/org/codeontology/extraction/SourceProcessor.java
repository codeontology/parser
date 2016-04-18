package org.codeontology.extraction;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class SourceProcessor extends AbstractProcessor<CtPackage> {
    @Override
    public void process(CtPackage pack) {
        WrapperFactory.getInstance().setParent(pack.getFactory());
        WrapperFactory.getInstance().wrap(pack).extract();
    }
}
