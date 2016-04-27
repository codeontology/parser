package org.codeontology.extraction;

import java.util.HashSet;
import java.util.Set;

public class WrapperRegister {
    private static WrapperRegister instance;
    private Set<String> register;
    private int size;
    public static final int LIMIT = 1000;

    private WrapperRegister() {
        register = new HashSet<>(LIMIT);
    }

    public static WrapperRegister getInstance() {
        if (instance == null) {
            instance = new WrapperRegister();
        }

        return instance;
    }

    public boolean add(Wrapper<?> wrapper) {
        handleSize();
        return register.add(wrapper.getRelativeURI());
    }

    private void handleSize() {
        size++;
        if (size > LIMIT) {
            size = 0;
            register = new HashSet<>(LIMIT);
        }
    }

}
