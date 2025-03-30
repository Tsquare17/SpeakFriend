package com.tsquare.speakfriend.utils;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.StringReader;

public class HTMLParser {
    public static String toText(String html) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            public boolean addNewLine;

            @Override
            public void handleText(final char[] data, final int pos) {
                String string = new String(data);
                stringBuilder.append(string.trim());
                addNewLine = true;
            }

            @Override
            public void handleStartTag(final HTML.Tag tag, final MutableAttributeSet attributeSet, final int pos) {
                if (addNewLine && (
                    tag == HTML.Tag.DIV
                    || tag == HTML.Tag.BR
                    || tag == HTML.Tag.P
                    || tag == HTML.Tag.LI
                    )
                ) {
                    stringBuilder.append("\n");
                    addNewLine = false;
                }
            }

            @Override
            public void handleSimpleTag(final HTML.Tag tag, final MutableAttributeSet attributeSet, final int pos) {
                handleStartTag(tag, attributeSet, pos);
            }
        };

        new ParserDelegator().parse(new StringReader(html), parserCallback, false);

        return stringBuilder.toString();
    }
}
