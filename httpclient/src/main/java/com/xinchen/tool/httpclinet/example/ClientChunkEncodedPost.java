package com.xinchen.tool.httpclinet.example;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 示例如何使用未缓冲的块编码的POST请求
 * <p>
 * Example how to use unbuffered chunk-encoded POST request.
 */
public class ClientChunkEncodedPost {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("File path not given");
            System.exit(1);
        }
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost("http://httpbin.org/post");

            File file = new File("args[0]");

            // content length is unknown (-1),HttpClient must use chunk coding
            InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);

            // 这里其实使用FileEntity更加合适,但是使用InputStreamEntity演示能从任意来源的流式传输数据
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            httppost.setEntity(reqEntity);

            System.out.println("Executing request: " + httppost.getRequestLine());
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        }
    }

}
