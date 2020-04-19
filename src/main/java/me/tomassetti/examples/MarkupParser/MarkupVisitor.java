package me.tomassetti.examples.MarkupParser;

import java.io.PrintStream;

public class MarkupVisitor extends MarkupParserBaseVisitor<String> {
    private final PrintStream stream;

    public MarkupVisitor(final PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public String visitFile(final MarkupParser.FileContext context) {
        visitChildren(context);

        stream.println();

        return null;
    }

    @Override
    public String visitTag(final MarkupParser.TagContext context) {
        StringBuilder text = new StringBuilder();
        String startDelimiter = "", endDelimiter = "";

        String id = context.ID(0).getText();

        switch (id) {
            case "b":
                startDelimiter = endDelimiter = "**";
                break;
            case "u":
                startDelimiter = endDelimiter = "*";
                break;
            case "quote":
                String attribute = context.attribute().STRING().getText();
                attribute = attribute.substring(1, attribute.length() - 1);
                startDelimiter = System.lineSeparator() + "> ";
                endDelimiter = System.lineSeparator() + "> " + System.lineSeparator() + "> – "
                    + attribute + System.lineSeparator();
                break;
        }

        text.append(startDelimiter);

        for (final MarkupParser.ElementContext node : context.element()) {
            if (node.tag() != null) {
                text.append(visitTag(node.tag()));
            }
            if (node.content() != null) {
                text.append(visitContent(node.content()));
            }
        }

        text.append(endDelimiter);

        return text.toString();
    }

    @Override
    public String visitContent(final MarkupParser.ContentContext context) {
        return context.getText();
    }

    @Override
    public String visitElement(final MarkupParser.ElementContext context) {
        if (context.parent instanceof MarkupParser.FileContext) {
            if (context.content() != null) {
                stream.print(visitContent(context.content()));
            }

            if (context.tag() != null) {
                stream.print(visitTag(context.tag()));
            }
        }

        return null;
    }
}
