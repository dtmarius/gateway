package com.dtmarius.gateway;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpRequestDto {

    public String url;
    public String method;
    public HashMap<String, ArrayList<String>> headerMap;
    public byte[] body;

}
