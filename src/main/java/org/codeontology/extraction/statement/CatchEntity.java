package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.BodyHolderEntity;
import org.codeontology.extraction.support.BodyTagger;
import org.codeontology.extraction.support.LineTagger;
import spoon.reflect.code.CtCatch;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class CatchEntity extends CodeElementEntity<CtCatch> implements BodyHolderEntity<CtCatch> {

    private int position;

    public CatchEntity(CtCatch catcher) {
        super(catcher);
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {
        tagBody();
        tagSourceCode();
        tagLine();
        tagCatchFormalParameters();
    }

    public void tagCatchFormalParameters() {
        List<TypeEntity<?>> formalParameters = getCatchFormalParameters();
        for (TypeEntity<?> catchFormalParameter : formalParameters) {
            getLogger().addTriple(this, Ontology.CATCH_FORMAL_PARAMETER_PROPERTY, catchFormalParameter);
            catchFormalParameter.follow();
        }
    }

    public List<TypeEntity<?>> getCatchFormalParameters() {
        List<CtTypeReference<?>> references = getElement().getParameter().getMultiTypes();
        List<TypeEntity<?>> parameters = new ArrayList<>();

        for (CtTypeReference<?> reference : references) {
            TypeEntity<?> parameter = getFactory().wrap(reference);
            parameter.setParent(getParent(ExecutableEntity.class));
            parameters.add(parameter);
        }

        return parameters;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public StatementEntity<?> getBody() {
        StatementEntity<?> body = getFactory().wrap(getElement().getBody());
        body.setParent(this);
        return body;
    }

    @Override
    public void tagBody() {
        new BodyTagger(this).tagBody();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }
}
