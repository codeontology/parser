package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.util.Set;


public class PackageExtractor extends Extractor<CtPackage> {

    public PackageExtractor(CtPackage pack) {
        super(pack);
    }

    @Override
    protected String getRelativeURI() {
        return getElement().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getPackageIndividual();
    }

    @Override
    public void extract() {
        if (getElement().getTypes().isEmpty()) {
            return;
        }

        tagType();
        tagName();
        tagComment();

        Set<CtType<?>> types = getElement().getTypes();
        for (CtType<?> current : types) {
            TypeExtractor<?> extractor = getFactory().getExtractor(current);
            tagPackage(extractor);
            extractor.extract();
        }
    }

    private void tagPackage(TypeExtractor<?> extractor) {
        addStatement(Ontology.getPackageProperty(), extractor.getResource());
    }
}