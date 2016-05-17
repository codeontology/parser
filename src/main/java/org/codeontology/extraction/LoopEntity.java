package org.codeontology.extraction;

import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;

public class LoopEntity<T extends CtLoop> extends StatementEntity<T> implements BodyHolderEntity<T> {

    public LoopEntity(T element) {
        super(element);
    }

    @Override
    public void extract() {
        super.extract();
        tagBody();
    }

    @Override
    public StatementEntity<?> getBody() {
        CtStatement statement = getElement().getBody();
        if (statement != null) {
            StatementEntity<?> body = getFactory().wrap(statement);
            body.setParent(this);
            return body;
        }

        return null;
    }

    @Override
    public void tagBody() {
        new BodyTagger(this).tagBody();
    }
}
