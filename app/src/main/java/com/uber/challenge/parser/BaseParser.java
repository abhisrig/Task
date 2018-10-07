package com.uber.challenge.parser;

/*
 * All the parsing related work must be delegated to this class's object
 * This will help in changing the parser library easily, as the client is not dependent on the concrete implementation
 * */
public abstract class BaseParser {

    /**
     * @param json  which needs to be parsed
     * @param model model supporting the json
     * @param <T>
     * @return
     */
    public abstract <T> T fromJson(String json, Class<T> model);

    public abstract <T> String toJson(T model);
}
