package org.codeontology.extraction;

import java.util.HashSet;
import java.util.Set;

public class EntityRegister {
    private static EntityRegister instance;
    private Set<String> register;
    private int size;
    private static final int SIZE = 2048;
    private static final int LOAD = SIZE / 2;

    private EntityRegister() {
        register = new HashSet<>(SIZE);
    }

    public static EntityRegister getInstance() {
        if (instance == null) {
            instance = new EntityRegister();
        }

        return instance;
    }

    public boolean add(Entity<?> entity) {
        handleSize();
        return register.add(entity.getRelativeURI());
    }

    private void handleSize() {
        size++;
        if (size > LOAD) {
            size = 0;
            register = new HashSet<>(SIZE);
        }
    }

}
