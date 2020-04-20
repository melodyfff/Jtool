package com.xinchen.tool.jsoup.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * 解析HTML
 *
 * @author xinchen
 * @version 1.0
 * @date 20/04/2020 11:18
 */
public class ParseHtml {

    public static void main(String[] args) throws IOException {
        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p></body></html>";

        // 转换字符串提取doc
        parseSimpleHtml(html);

        System.out.println();

        // 转换一个body片段
        parseSimpleHtmlBody(html);


        // 根据url加载html
        parseSimpleHtmlByUrl("http://bing.com");


        // 从本地文件中转换html
        String filePath = Objects.requireNonNull(ParseHtml.class.getClassLoader().getResource("Hello.html")).getFile();
        parseSimpleHtmlByFile(new File(filePath));
    }

    private static void parseSimpleHtml(String html) {
        Document doc = Jsoup.parse(html);
        System.out.println(doc);
    }

    private static void parseSimpleHtmlBody(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        // 等同于doc.getElementsByTag("body")
        Element body = doc.body();
        System.out.println(body);
    }

    private static void parseSimpleHtmlByUrl(final String url) throws IOException {
        final Document doc = Jsoup.connect(url)
                // 单位ms
                .timeout(5000)
                // 请求方式为GET
                .get();
        System.out.println(doc);
    }

    private static void parseSimpleHtmlByFile(File file) throws IOException {
        final Document doc = Jsoup.parse(file, "UTF-8", "http://hello.com");
        System.out.println(doc);
    }
}
