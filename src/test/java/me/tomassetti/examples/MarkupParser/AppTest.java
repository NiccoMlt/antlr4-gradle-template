package me.tomassetti.examples.MarkupParser;

import org.antlr.v4.runtime.TokenStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
class AppTest {
    MarkupErrorListener errorListener;

    @BeforeEach
    void initListener() {
        final StringWriter writer = new StringWriter();
        this.errorListener = new MarkupErrorListener(writer);
    }

    @Test
    @DisplayName("Test that simple text is recognized correctly")
    public void testText() {
        final MarkupLexer markupLexer = setupLexer("anything in here");
        final MarkupParser markupParser = setupParser(markupLexer);
        final MarkupParser.ContentContext context = markupParser.content();

        assertNull(context.exception);
        assertEquals("", this.errorListener.getSymbol());
    }

    @Test
    @DisplayName("Test that not valid text fails correctly")
    public void testInvalidText() {
        final MarkupLexer markupLexer = setupLexer("[anything in here");
        final MarkupParser markupParser = setupParser(markupLexer);

        final MarkupParser.ContentContext context = markupParser.content();

        assertNotNull(context.exception);

        // note that this.errorListener.symbol could be empty
        // when ANTLR doesn't recognize the token or there is no error.
        // In such cases check the output of errorListener
        assertEquals("[", this.errorListener.getSymbol());
    }

    @Test
    @DisplayName("Test that default mode makes the correct parsing of an attribute impossible")
    public void testWrongMode() {
        final String s = "author=\"john\"";
        final MarkupLexer markupLexer = setupLexer(s);
        final MarkupParser markupParser = setupParser(markupLexer);

        final MarkupParser.AttributeContext context = markupParser.attribute();
        final TokenStream ts = markupParser.getTokenStream();

        assertNotNull(context.exception);

        assertEquals(MarkupLexer.DEFAULT_MODE, markupLexer._mode);
        assertEquals(MarkupLexer.TEXT, ts.get(0).getType());
        assertEquals(s, this.errorListener.getSymbol());
    }

    @Test
    @DisplayName("Test that BBCode mode let us check a correct attribute")
    public void testAttribute() {
        final MarkupLexer markupLexer = setupLexer("author=\"john\"");
        final MarkupParser markupParser = setupParser(markupLexer);

        // we have to manually push the correct mode
        markupLexer.pushMode(MarkupLexer.BBCODE);

        final MarkupParser.AttributeContext context = markupParser.attribute();
        final TokenStream ts = markupParser.getTokenStream();

        assertNull(context.exception);

        assertEquals(MarkupLexer.ID, ts.get(0).getType());
        assertEquals(MarkupLexer.EQUALS, ts.get(1).getType());
        assertEquals(MarkupLexer.STRING, ts.get(2).getType());

        assertEquals("", this.errorListener.getSymbol());
    }

    @Test
    @DisplayName("Test that BBCode mode let us check a wrong attribute")
    public void testInvalidAttribute() {
        final MarkupLexer markupLexer = setupLexer("author=/\"john\"");
        final MarkupParser markupParser = setupParser(markupLexer);

        // we have to manually push the correct mode
        markupLexer.pushMode(MarkupLexer.BBCODE);

        MarkupParser.AttributeContext context = markupParser.attribute();

        assertEquals("/", this.errorListener.getSymbol());
    }

    private MarkupLexer setupLexer(final String input) {
        final MarkupLexer markupLexer = App.setupLexer(input);
        markupLexer.removeErrorListeners();
        markupLexer.addErrorListener(this.errorListener);
        return markupLexer;
    }

    private MarkupParser setupParser(final MarkupLexer markupLexer) {
        final MarkupParser markupParser = App.setupParser(markupLexer);
        markupParser.removeErrorListeners();
        markupParser.addErrorListener(this.errorListener);
        return markupParser;
    }
}
