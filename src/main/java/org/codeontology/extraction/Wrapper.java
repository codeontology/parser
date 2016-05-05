package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;

public interface Wrapper<E extends CtElement> {

    String SEPARATOR = "-";

    void setReference(CtReference reference);

    E getElement();

    void extract();

    void setElement(E element);

    Model getModel();

    WrapperFactory getFactory();

    CtReference getReference();

    boolean isDeclarationAvailable();

    RDFLogger getLogger();

    Wrapper<?> getParent();

    void setParent(Wrapper<?> parent);

    void follow();

    String getRelativeURI();

    Resource getResource();
}
