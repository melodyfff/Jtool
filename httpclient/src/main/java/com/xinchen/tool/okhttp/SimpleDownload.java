package com.xinchen.tool.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 简单下载器
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/12/25 22:03
 */
@Slf4j
public class SimpleDownload {
    static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) {
        String dirPre = "";
        for (int i = 1; i < 81; i++) {
            downLoad("url-"+i+".jpg",dirPre);
        }

    }

    static void downLoad(String url,String dirPre){
        Call call = client.newCall(new Request.Builder().url(url).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error(">>> Down Fail > {} ,",call.request().url().url(),e);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                byte[] bytes = Objects.requireNonNull(response.body()).bytes();

                // 文件名定义
                File file = new File(dirPre + call.request().url().pathSegments().get(1));

                bytes2file(bytes,file);

                log.info("{} down success!",call.request().url().url());
            }
        });
    }

    static void bytes2file(byte[] bytes,File file) throws IOException {
        try (FileImageOutputStream imageOutput = new FileImageOutputStream(file)){
            imageOutput.write(bytes,0,bytes.length);
        }
    }
}
