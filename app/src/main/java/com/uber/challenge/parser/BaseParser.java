package com.uber.challenge.parser;

public abstract class BaseParser {

    public abstract <T> T fromJson(String json, Class<T> model);

    public abstract <T> String toJson(T model);
}
