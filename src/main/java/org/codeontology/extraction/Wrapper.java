package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import spoon.reflect.declaration.CtElement;

public interface Wrapper<E extends CtElement> {

    String SEPARATOR = "-";

    E getElement();

    void extract();

    Model getModel();

    WrapperFactory getFactory();

    RDFLogger getLogger();

    Wrapper<?> getParent();

    void setParent(Wrapper<?> parent);

    void follow();

    String getRelativeURI();

    Resource getResource();
}
