package org.codeontology.commentparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocCommentParser {
    String comment;
    private List<Tag> tags;
    private boolean parsed;
    public static final String REGEXP = "^\\s*(@\\w+)\\s+(.*)$";
    public static final Pattern PATTERN = Pattern.compile(REGEXP, Pattern.DOTALL);

    public DocCommentParser(String comment) {
        setComment(comment);
        tags = new ArrayList<>();
    }

    private void setComment(String comment) {
        parsed = false;
        this.comment = comment;
    }

    public void parse() {
        Scanner scanner = new Scanner("\n" + removeDescription());
        scanner.useDelimiter("\\n\\s*@");
        while (scanner.hasNext()) {
            String current = "@" + scanner.next();
            Matcher matcher = getMatcher(current);
            if (matcher.matches()) {
                String name = matcher.group(1);
                String text = matcher.group(2);
                Tag tag = TagFactory.getInstance().createTag(name, text);
                tags.add(tag);
            }
        }
        parsed = true;
    }

    private String removeDescription() {
        Scanner scanner = new Scanner(comment);
        while (scanner.hasNextLine()) {
            String current = scanner.nextLine();
            if (getMatcher(current).matches()) {
                scanner.useDelimiter("\\Z");
                if (scanner.hasNext()) {
                    current += "\n" + scanner.next();
                }
                scanner.close();
                return current;
            }
        }
        scanner.close();
        return "";
    }

    public List<Tag> getTags() {
        if (!parsed) {
            parse();
        }

        return tags;
    }

    public List<Tag> getTags(String name) {
        if (!parsed) {
            parse();
        }

        List<Tag> result = new ArrayList<>();

        for (Tag tag : tags) {
            if (tag.getName().equals(name)) {
                result.add(tag);
            }
        }

        return result;
    }

    private Matcher getMatcher(String current) {
        return PATTERN.matcher(current);
    }


}
