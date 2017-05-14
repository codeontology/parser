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

package org.codeontology.extraction.project;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.AbstractEntity;
import org.codeontology.extraction.EntityFactory;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.declaration.PackageEntity;
import spoon.reflect.reference.CtPackageReference;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileEntity extends AbstractEntity<JarFile> {

    private Collection<PackageEntity> packs;
    private Map<Package, List<Class<?>>> map;

    public JarFileEntity(JarFile element) {
        super(element);
        setPackages();
    }

    @Override
    protected String buildRelativeURI() {
        return getName() + SEPARATOR + packs.hashCode();
    }

    public String getName() {
        return new File(getElement().getName()).getName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.JAR_FILE_ENTITY;
    }

    @Override
    public void extract() {
        System.out.println("Running on " + getElement().getName());
        if (CodeOntology.extractProjectStructure()) {
            tagName();
            tagType();
            tagDependency();
        }
        tagPackages();
        System.out.println("Triples extracted successfully.");
    }

    public void tagDependency() {
        ProjectEntity<?> mainProject = CodeOntology.getProject();
        if (mainProject != null) {
            getLogger().addTriple(mainProject, Ontology.DEPENDENCY_PROPERTY, this);
        }
    }

    public void tagPackages() {
        packs.forEach(PackageEntity::extract);
    }

    private void setPackages() {
        packs = new ArrayList<>();
        buildMap();
        Set<Package> packages = map.keySet();
        for (Package pack : packages) {
            CtPackageReference packageReference = ReflectionFactory.getInstance().createPackageReference(pack);
            PackageEntity entity = EntityFactory.getInstance().wrap(packageReference);
            entity.setTypes(map.get(pack));
            entity.setParent(this);
            packs.add(entity);
        }
    }

    public void tagName() {
        String name = getName();
        Literal label = getModel().createTypedLiteral(name);
        getLogger().addTriple(this, Ontology.RDFS_LABEL_PROPERTY, label);
    }

    private void buildMap() {
        System.out.println("Analyzing file " + getElement().getName());
        Enumeration entries = getElement().entries();
        map = new HashMap<>();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String entryPath = entry.getName();
            if (entryPath.endsWith(".class")) {
                String typeName = entry.getName().replace("/", ".").substring(0, entryPath.length() - 6);
                try {
                    Class<?> clazz = Class.forName(typeName);
                    Package pack = clazz.getPackage();
                    List<Class<?>> types = map.get(pack);
                    if (pack != null) {
                        if (types == null) {
                            types = new ArrayList<>();
                        }
                        types.add(clazz);
                        map.put(pack, types);
                    }
                } catch (Throwable e) {
                    // Cannot get a class object from this jar entry
                    // we just skip this entry
                }
            }
        }
    }
}