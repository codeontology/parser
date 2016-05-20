package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.ProjectProcessor;
import org.codeontology.buildsystems.Project;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

public class PackageEntity extends NamedElementEntity<CtPackage> {

    private Collection<TypeEntity<?>> types;

    public PackageEntity(CtPackage pack) {
        super(pack);
    }

    public PackageEntity(CtPackageReference pack) {
        super(pack);
    }

    @Override
    public String buildRelativeURI() {
        String relativeURI = getPackageName();
        return relativeURI.replace(" ", SEPARATOR);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PACKAGE_ENTITY;
    }

    @Override
    public void extract() {
        Collection<TypeEntity<?>> types = getTypes();

        if (types.isEmpty()) {
            return;
        }

        tagType();
        tagName();
        tagPackageOf();
        tagParent();

        if (isDeclarationAvailable()) {
            tagComment();
        }
    }

    public void tagParent() {
        if (CodeOntology.extractProjectStructure()) {
            getLogger().addTriple(this, Ontology.DECLARED_BY_PROPERTY, getParent());
        }
    }

    public void tagPackageOf() {
        for (TypeEntity type : types) {
            getLogger().addTriple(this, Ontology.PACKAGE_OF_PROPERTY, type);
            if (CodeOntology.verboseMode()) {
                System.out.println("Running on " + type.getReference().getQualifiedName());
            }
            type.extract();
        }
    }

    public Collection<TypeEntity<?>> getTypes() {
        if (types == null) {
            setTypes();
        }
        return types;
    }

     private void setTypes() {
         types = new ArrayList<>();
         if (isDeclarationAvailable()) {
             Set<CtType<?>> ctTypes = getElement().getTypes();
             for (CtType current : ctTypes) {
                 types.add(getFactory().wrap(current));
             }
         }
     }

    public void setTypes(List<Class<?>> types) {
        this.types = new ArrayList<>();
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