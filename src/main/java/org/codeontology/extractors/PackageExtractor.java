package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
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
        return Ontology.PACKAGE_CLASS;
    }

    @Override
    public void extract() {
        Set<CtType<?>> types = getElement().getTypes();

        if (types.isEmpty()) {
            return;
        }

        tagType();
        tagName();
        tagComment();
        tagPackageOf(types);
    }

    protected void tagPackageOf(Set<CtType<?>> types) {
        for (CtType<?> current : types) {
            TypeExtractor<?> extractor = getFactory().getExtractor(current);
            addStatement(Ontology.PACKAGE_OF_PROPERTY, extractor.getResource());
            extractor.extract();
        }
    }
}