package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import spoon.reflect.declaration.CtElement;

public interface Entity<E extends CtElement> {

    String SEPARATOR = "-";

    E getElement();

    void extract();

    Model getModel();

    EntityFactory getFactory();

    RDFLogger getLogger();

    Entity<?> getParent();

    void setParent(Entity<?> parent);

    void follow();

    String getRelativeURI();

    Resource getResource();
}
