package org.codeontology.extraction;

import java.util.List;

public interface ModifiableWrapper {

    List<Modifier> getModifiers();

    void tagModifiers();

}
