package com.uber.challenge.image;

import java.util.Map;

public class ResponseData {
    Map<String, String> responseHeaderMap;
    byte[] data;

    public ResponseData(byte[] data, Map<String, String> headerMap) {
        this.data = data;
        this.responseHeaderMap = headerMap;
    }
}
