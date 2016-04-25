package org.codeontology.extraction;

import java.util.HashSet;
import java.util.Set;

public class WrapperRegister {
    private static WrapperRegister instance;
    private Set<String> register;

    private WrapperRegister() {
        register = new HashSet<>();
    }

    public static WrapperRegister getInstance() {
        if (instance == null) {
            instance = new WrapperRegister();
        }

        return instance;
    }

    public boolean add(Wrapper<?> wrapper) {
        System.out.println("WrapperRegister size: " + register.size());
        return register.add(wrapper.getRelativeURI());
    }

}
