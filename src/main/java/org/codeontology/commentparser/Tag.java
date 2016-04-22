package org.codeontology.commentparser;

public class Tag {
    private String text;
    private String name;

    public Tag(String name, String text) {
        setName(name);
        getText(text);
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

    public void getText(String text) {
        this.text = text.trim().replaceAll("\\s+", " ");
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    protected String[] splitText() {
        return text.split(" ", 2);
    }
}

