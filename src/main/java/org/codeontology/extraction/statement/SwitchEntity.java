package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtSwitch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SwitchEntity extends StatementEntity<CtSwitch<?>> {

    public SwitchEntity(CtSwitch element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.SWITCH_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagSwitchLabels();
    }

    public void tagSwitchLabels() {
        List<SwitchLabelEntity> labels = getSwitchLabels();
        for (SwitchLabelEntity label : labels) {
            getLogger().addTriple(this, Ontology.SWITCH_LABEL_PROPERTY, label);
            label.extract();
        }
    }

    public List<SwitchLabelEntity> getSwitchLabels() {
        List<CtCase<?>> labels = new ArrayList<>(getElement().getCases());
        List<SwitchLabelEntity> result = new ArrayList<>();

        Iterator<CtCase<?>> iterator = labels.iterator();
        if (iterator.hasNext()) {
            SwitchLabelEntity previous = getFactory().wrap(iterator.next());
            previous.setParent(this);
            result.add(previous);

            while (iterator.hasNext()) {
                SwitchLabelEntity current = getFactory().wrap(iterator.next());
                current.setParent(this);
                result.add(current);
                previous.setNext(current);
            }
        }

        return result;
    }


}
