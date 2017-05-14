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

package org.codeontology.extraction.statement;

import org.codeontology.extraction.support.BodyHolderEntity;
import org.codeontology.extraction.support.BodyTagger;
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