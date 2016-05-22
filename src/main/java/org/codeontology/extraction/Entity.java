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