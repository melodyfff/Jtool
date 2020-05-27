package com.xinchen.tool.httptrace.api;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xinchen
 * @version 1.0
 * @date 27/05/2020 11:16
 */
@RestController
@RequestMapping("/v1")
public class HelloApi {
    private static Gson gson = new Gson();

    // 生成新的Hello对象
    private static final Function<Integer,Hello> HELLO_BUILD = Hello::new;

    @GetMapping("/ok")
    public ResponseEntity ok(){
        final int time = ThreadLocalRandom.current().nextInt(100);
        List<Hello> list = Arrays
                .stream(new int[time])
                .mapToObj(HELLO_BUILD::apply)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gson.toJson(list));
    }


    @Data
    static class Hello {
        private String message;
        Hello(int message) {
            this.message = "ok"+message;
        }
    }
}
