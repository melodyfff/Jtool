package com.xinchen.tool.httptrace;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 * @author xinchen
 * @version 1.0
 * @date 27/05/2020 11:08
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(App.class)
                .bannerMode(Banner.Mode.OFF)
                .run();
    }
}
