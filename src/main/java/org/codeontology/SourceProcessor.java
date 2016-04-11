package org.codeontology;

import org.codeontology.extraction.ExtractorFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;

public class SourceProcessor extends AbstractProcessor<CtPackage> {
    @Override
    public void process(CtPackage pack) {
        ExtractorFactory.getInstance().getExtractor(pack).extract();
    }
}
