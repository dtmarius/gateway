package com.dtmarius.gateway;

import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class EncodingDetectionTest {

    @Test
    public void detectUTF8_InHtml() throws IOException, ExecutionControl.NotImplementedException {
        String inputText = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head></html>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputText.getBytes(StandardCharsets.UTF_8));
        throw new ExecutionControl.NotImplementedException("");
    }

}
