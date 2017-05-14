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

package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface Entity<E> {

    String SEPARATOR = "-";

    E getElement();

    void extract();

    Model getModel();

    EntityFactory getFactory();

    RDFLogger getLogger();

    Entity<?> getParent();

    Entity<?> getParent(Class<?>... classes);

    void setParent(Entity<?> parent);

    void follow();

    String getRelativeURI();

    Resource getResource();
}