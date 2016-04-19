package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeWrapper extends TypeWrapper<CtType<?>> {

    public ParameterizedTypeWrapper(CtTypeReference reference) {
        super(reference);
    }

    @Override
    public String getRelativeURI() {
        List<CtTypeReference<?>> arguments = getReference().getActualTypeArguments();
        String uri = getReference().getQualifiedName();

        for (CtTypeReference<?> argument : arguments) {
            uri = uri + SEPARATOR + argument;
        }

        uri = uri.replace(" ", "_");

        return uri;
    }

    @Override
    public void extract() {
        tagType();
        tagRawType();
        tagActualTypeArguments();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETERIZED_TYPE_CLASS;
    }

    protected void tagRawType() {
        CoreFactory coreFactory = getReference().getFactory().Core();
        CtTypeReference<?> cloneReference = coreFactory.clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        TypeWrapper rawType = getFactory().wrap(cloneReference);
        RDFWriter.addTriple(this, Ontology.RAW_TYPE_PROPERTY, rawType);
    }

    protected void tagActualTypeArguments() {

    }
}
