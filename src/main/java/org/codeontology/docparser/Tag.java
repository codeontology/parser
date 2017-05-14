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

package org.codeontology.docparser;

import java.util.Arrays;

public class Tag {
    private String text;
    private String name;

    public Tag(String name, String text) {
        setName(name);
        setText(text);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName() + " " + getText();
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text.trim().replaceAll("\\s+", " ");
    }

    private void setName(String name) {
        this.name = name.trim();
    }

    protected String[] splitText() {
        return ensureSize(text.split(" ", 2), 2);
    }

    private String[] ensureSize(String[] array, int size) {
        String[] result;

        if (array.length >= size) {
            result = array;
        } else {
            result = new String[size];
            Arrays.fill(result, "");
            System.arraycopy(array, 0, result, 0, array.length);
        }

        return result;
    }
}
