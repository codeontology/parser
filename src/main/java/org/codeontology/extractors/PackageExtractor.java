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
        return Ontology.getPackageIndividual();
    }

    @Override
    public void extract() {
        Set<CtType<?>> types = getElement().getTypes();

        if (types.isEmpty()) {
            return;
        }

        System.out.println("Extracting triples for package " + getElement().getQualifiedName());
        tagType();
        tagName();
        tagComment();

        for (CtType<?> current : types) {
            TypeExtractor<?> extractor = getFactory().getExtractor(current);
            tagPackage(extractor);
            extractor.extract();
        }
        System.out.println("Done with package " + getElement().getQualifiedName());
    }

    private void tagPackage(TypeExtractor<?> extractor) {
        addStatement(Ontology.getPackageProperty(), extractor.getResource());
    }
}