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