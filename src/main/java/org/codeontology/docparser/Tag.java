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

