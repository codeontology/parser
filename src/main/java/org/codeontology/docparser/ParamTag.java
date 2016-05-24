package org.codeontology.docparser;

public class ParamTag extends Tag {

    private String parameterName;
    private String parameterComment;
    public static final String TAG = "@param";

    public ParamTag(String text) {
        super(TAG, text);
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
