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
import org.codeontology.extraction.RDFLogger;

public class TargetedLabelTagger {

    private FlowBreakerEntity<?> entity;

    public TargetedLabelTagger(FlowBreakerEntity<?> entity) {
        this.entity = entity;
    }

    public void tagTargetedLabel() {
        String labelString = entity.getTargetedLabel();
        if (labelString != null) {
            Literal label = entity.getModel().createTypedLiteral(labelString);
            RDFLogger.getInstance().addTriple(entity, Ontology.TARGETED_LABEL_PROPERTY, label);
        }
    }
}