package com.xinchen.tool.jsoup.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 提取HTML
 *
 * @author xinchen
 * @version 1.0
 * @date 20/04/2020 15:18
 */
public class ExtractHtml {

    public static void main(String[] args) throws IOException {

        // 使用DOM方法来遍历一个文档
        extractByDomMethods();

        // 使用Selector的方式
        extractBySelector();

        // 关于URL的处理
        extractUrl();
    }

    private static void extractByDomMethods() throws IOException {
        final Document doc = Jsoup.connect("http://bing.com")
                .timeout(5000)
                .get();

        final Element content = doc.getElementById("content");
        System.out.println("content: " + content);

        final Elements links = doc.getElementsByTag("a");

        links.forEach(link -> {
            String linkHref = link.attr("href");
            String lingText = link.text();
            System.out.println("href: " + linkHref);
            System.out.println("text: " + lingText);
        });
    }

    private static void extractBySelector() throws IOException {
        final Document doc = Jsoup.connect("http://bing.com")
                .timeout(5000)
                .get();

        final Elements links = doc.select("a[href]");
        final Elements pngs = doc.select("img[src$=.png]");

        System.out.println(links);
        System.out.println(pngs);
    }

    private static void extractUrl() throws IOException {
        Document doc = Jsoup.connect("http://jsoup.org").get();

        Element link = doc.select("a").first();
        // == "/"
        String relHref = link.attr("href");
        // "http://jsoup.org/"
        String absHref = link.attr("abs:href");

        System.out.println(relHref);
        System.out.println(absHref);
    }
}
