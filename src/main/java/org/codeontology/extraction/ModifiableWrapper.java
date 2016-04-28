package org.codeontology.extraction;

import java.util.List;

public interface ModifiableWrapper {

    List<ModifierClass> getModifiers();

    void tagModifiers();

}
