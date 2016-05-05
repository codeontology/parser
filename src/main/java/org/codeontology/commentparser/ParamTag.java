package org.codeontology.commentparser;

public class ParamTag extends Tag {

    private String parameterName;
    private String parameterComment;

    public ParamTag(String name, String text) {
        super(name, text);
        String[] values = splitText();
        parameterName = values[0];
        parameterComment = values[1];
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterComment() {
        return parameterComment;
    }
}
