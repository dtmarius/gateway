/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dtmarius.gateway;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 *
 * @author dtmarius.com
 */
public class RegexNamedCapturingGroupTest {

    @Test
    public void test2() {
        String input = "https://localhost/backend1/abc";

        String regex = "(?<protocol>https?)://localhost/backend1(?<path>.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        System.out.println("start");
        while (matcher.find()) {
            String protocol = matcher.group("protocol");
            System.out.println("protocol: " + protocol);
            String path = matcher.group("path");
            System.out.println("path: " + path);
        }

        System.out.println("ende");
    }

    @Test
    public void test1() {
        String input = "hello 12345 demo";

        String regex = "(?<zipCode>[1-9]{5})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        System.out.println("start");
        while (matcher.find()) {
            String zipCode = matcher.group("zipCode");
            System.out.println("zipCode:_" + zipCode);
            String replacementTest = matcher.replaceAll("..${zipCode}..");
            System.out.println("replacementTest:_" + replacementTest);
        }

        System.out.println("ende");
    }

    @Test
    public void test3() {
        String input = "http://dtmarius.com/backend1/todos?sort=date";

        String regex = "^(?<protocol>https?)://dtmarius.com/backend1/(?<path>.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        System.out.println("start");
        if (matcher.find()) {
            String replacementTest = matcher.replaceAll("${protocol}://example.com/${path}");
            System.out.println("replacementTest:_" + replacementTest);
        }

        System.out.println("ende");
    }
}
