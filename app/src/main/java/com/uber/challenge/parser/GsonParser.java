package com.uber.challenge.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public class GsonParser extends BaseParser {
    private final Gson mGson;

    public GsonParser() {
        mGson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).
                excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public <T> T fromJson(String json, Class<T> model) {
        return mGson.fromJson(json, model);
    }

    @Override
    public <T> String toJson(T model) {
        return mGson.toJson(model);
    }
}
