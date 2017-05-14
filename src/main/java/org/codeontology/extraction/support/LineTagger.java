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

package org.codeontology.extraction.support;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.RDFLogger;
import spoon.reflect.cu.SourcePosition;

public class LineTagger {
    private CodeElementEntity<?> entity;
    private SourcePosition position;

    public LineTagger(CodeElementEntity<?> entity) {
        this.entity = entity;
        position = entity.getElement().getPosition();
    }

    public void tagLine() {
        if (position == null) {
            return;
        }
        Literal line = entity.getModel().createTypedLiteral(position.getLine());
        RDFLogger.getInstance().addTriple(entity, Ontology.LINE_PROPERTY, line);
    }

    public void tagEndLine() {
        if (position == null) {
            return;
        }

        Literal endLine = entity.getModel().createTypedLiteral(position.getEndLine());
        RDFLogger.getInstance().addTriple(entity, Ontology.END_LINE_PROPERTY, endLine);
    }
}