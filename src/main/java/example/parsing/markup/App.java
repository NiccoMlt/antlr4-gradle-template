package example.parsing.markup;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class App {

    public static void main(final String[] args) {
        final MarkupParser markupParser = setupParser(setupLexer(
            "I would like to [b]emphasize[/b] this and [u]underline [b]that[/b][/u]. " +
                "Let's not forget to quote: [quote author=\"John\"]You're wrong![/quote]"
        ));

        final MarkupParser.FileContext fileContext = markupParser.file();
        final MarkupVisitor visitor = new MarkupVisitor(System.out);
        visitor.visit(fileContext);
    }

    public static MarkupLexer setupLexer(final String input) {
        final CharStream inputStream = CharStreams.fromString(input);
        return new MarkupLexer(inputStream);
    }

    public static MarkupParser setupParser(final MarkupLexer markupLexer) {
        final CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        return new MarkupParser(commonTokenStream);
    }
}
