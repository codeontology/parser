package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.Set;


public class PackageWrapper extends Wrapper<CtPackage> {

    private Set<TypeWrapper<?>> types;

    public PackageWrapper(CtPackage pack) {
        super(pack);
    }

    public PackageWrapper(CtPackageReference pack) {
        super(pack);
    }

    @Override
    public String buildRelativeURI() {
        String relativeURI = getPackageName();
        return relativeURI.replace(" ", SEPARATOR);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PACKAGE_CLASS;
    }

    @Override
    public void extract() {
        Set<TypeWrapper<?>> types = getTypes();

        if (types.isEmpty()) {
            return;
        }

        tagType();
        tagName();
        tagPackageOf();

        if (isDeclarationAvailable()) {
            tagComment();
        }
    }

    public void tagPackageOf() {
        for (TypeWrapper type : types) {
            getLogger().addTriple(this, Ontology.PACKAGE_OF_PROPERTY, type);
            if (CodeOntology.verboseMode()) {
                System.out.println("Extracting triples for " + type.getReference().getQualifiedName());
            }
            type.extract();
        }
    }

    public Set<TypeWrapper<?>> getTypes() {
        if (types == null) {
            setTypes();
        }
        return types;
    }

     private void setTypes() {
         types = new HashSet<>();
         if (isDeclarationAvailable()) {
             Set<CtType<?>> ctTypes = getElement().getTypes();
             for (CtType current : ctTypes) {
                 types.add(getFactory().wrap(current));
             }
         }
     }

    public void setTypes(Set<Class<?>> types) {
        this.types = new HashSet<>();
        for (Class<?> type : types) {
            CtTypeReference<?> reference = ReflectionFactory.getInstance().createTypeReference(type);
            this.types.add(getFactory().wrap(reference));
        }
    }

    private String getPackageName() {
        if (isDeclarationAvailable()) {
            return getElement().getQualifiedName();
        } else {
            return ((CtPackageReference) getReference()).getActualPackage().getName();
        }
    }
}