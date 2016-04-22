package org.codeontology.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamTagParser {

    private String commentString;
    private List<ParamTag> tagList;

    public ParamTagParser(String commentString) {
        this.commentString = commentString;
        tagList = new ArrayList<>();
    }

    void parse() {
        final int IN_TEXT = 1;
        final int TAG_GAP = 2;
        final int TAG_NAME = 3;
        int state = TAG_GAP;
        boolean newLine = true;
        String tagName = null;
        int tagStart = 0;
        int textStart = 0;
        int lastNonWhite = -1;
        int len = commentString.length();
        for (int inx = 0; inx < len; ++inx) {
            char ch = commentString.charAt(inx);
            boolean isWhite = Character.isWhitespace(ch);
            switch (state)  {
                case TAG_NAME:
                    if (isWhite) {
                        tagName = commentString.substring(tagStart, inx);
                        state = TAG_GAP;
                    }
                    break;
                case TAG_GAP:
                    if (isWhite) {
                        break;
                    }
                    textStart = inx;
                    state = IN_TEXT;
                case IN_TEXT:
                    if (newLine && ch == '@') {
                        parseCommentComponent(tagName, textStart,
                                lastNonWhite+1);
                        tagStart = inx;
                        state = TAG_NAME;
                    }
                    break;
            }
            if (ch == '\n') {
                newLine = true;
            } else if (!isWhite) {
                lastNonWhite = inx;
                newLine = false;
            }
        }
        switch (state)  {
            case TAG_NAME:
                tagName = commentString.substring(tagStart, len);
            case TAG_GAP:
                textStart = len;
            case IN_TEXT:
                parseCommentComponent(tagName, textStart, lastNonWhite+1);
                break;
        }
    }

    void parseCommentComponent(String tagName, int from, int upto) {
        String tx = upto <= from ? "" : commentString.substring(from, upto);
        if (tagName != null && tagName.equals("@param")) {
            ParamTag tag = new ParamTag(tx);
            tagList.add(tag);
        }
    }

    public List<ParamTag> paramTags() {
        List<ParamTag> found = new ArrayList<>();
        for (ParamTag next : tagList) {
            if (!next.isTypeParameter()) {
                found.add(next);
            }
        }
        return found;
    }
}


class ParamTag {
    private static final Pattern typeParamRE = Pattern.compile("<([^<>]+)>");

    private final String parameterName;
    private final String parameterComment;
    private final boolean isTypeParameter;
    private final String text;

    public ParamTag(String text) {
        this.text = text;
        String[] sa = divideAtWhite();
        Matcher m = typeParamRE.matcher(sa[0]);
        isTypeParameter = m.matches();
        parameterName = isTypeParameter ? m.group(1) : sa[0];
        parameterComment = sa[1];
    }

    public boolean isTypeParameter() {
        return isTypeParameter;
    }

    private String[] divideAtWhite() {
        String[] sa = new String[2];
        int len = text.length();
        sa[0] = text;
        sa[1] = "";
        for (int inx = 0; inx < len; ++inx) {
            char ch = text.charAt(inx);
            if (Character.isWhitespace(ch)) {
                sa[0] = text.substring(0, inx);
                for (; inx < len; ++inx) {
                    ch = text.charAt(inx);
                    if (!Character.isWhitespace(ch)) {
                        sa[1] = text.substring(inx, len);
                        break;
                    }
                }
                break;
            }
        }
        return sa;
    }

    public String getParameterComment() {
        return parameterComment;
    }

    public String getParameterName() {
        return parameterName;
    }
}
