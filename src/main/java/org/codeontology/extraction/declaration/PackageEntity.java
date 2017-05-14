/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
        tagLabel();
        tagTypes();
        tagParent();

        if (isDeclarationAvailable()) {
            tagComment();
        }
    }

    public void tagParent() {
        if (CodeOntology.extractProjectStructure()) {
            getLogger().addTriple(this, Ontology.PROJECT_PROPERTY, getParent());
        }
    }

    public void tagTypes() {
        for (TypeEntity type : types) {
            getLogger().addTriple(this, Ontology.IS_PACKAGE_OF_PROPERTY, type);
            getLogger().addTriple(type, Ontology.HAS_PACKAGE_PROPERTY, this);
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
             ctTypes.stream().map(current -> getFactory().wrap(current)).forEach(types::add);
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