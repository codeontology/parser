package org.codeontology.extraction;

import java.util.HashSet;
import java.util.Set;

public class WrapperRegister {
    private static WrapperRegister instance;
    private Set<String> register;
    private int size;
    private static final int SIZE = 2048;
    private static final int LOAD = SIZE / 2;

    private WrapperRegister() {
        register = new HashSet<>(SIZE);
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
        if (size > LOAD) {
            size = 0;
            register = new HashSet<>(SIZE);
        }
    }

}
