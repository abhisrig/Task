package com.uber.challenge.parser;

/**
 * Factory for providing parser instance to app
 */
public class ParserFactory {

    public static BaseParser getParser() {
        return new GsonParser();
    }
}
