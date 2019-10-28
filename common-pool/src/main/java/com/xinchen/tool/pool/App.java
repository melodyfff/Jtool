package com.xinchen.tool.pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");

        BufferedReader bud = new BufferedReader(new StringReader("this is a test \nhello"));

        PrintWriter pr = new PrintWriter(System.out);

        bud.lines().forEach(pr::println);

        pr.flush();

        pr.close();
        bud.close();
    }
}
