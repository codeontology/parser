package org.codeontology.commentparser;


public class TagFactory {
    private static TagFactory instance;

    private TagFactory() {

    }

    public static TagFactory getInstance() {
        if (instance == null) {
            instance = new TagFactory();
        }

        return instance;
    }

    public Tag createTag(String name, String text) {
        if (name.equals("@param")) {
            return new ParamTag(name, text);
        } else {
            return new Tag(name, text);
        }
    }
}
