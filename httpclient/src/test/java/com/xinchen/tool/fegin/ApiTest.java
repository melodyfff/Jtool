package com.xinchen.tool.fegin;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.RequestLine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;


/**
 * @author xinchen
 * @version 1.0
 * @date 08/09/2020 14:53
 */
public class ApiTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void test1(){
        server.enqueue(new MockResponse().setBody("hello"));
        server.enqueue(new MockResponse().setBody("world"));

        final TestApi testApi = Api.builder(TestApi.class, "http://localhost:"+server.getPort()).build();

        assertThat(testApi.call()).isEqualTo("hello");
        assertThat(testApi.hyCall().execute()).isEqualTo("world");
    }

    @Test
    public void testOkHttp(){
        final TestApi testApi = Api.builder(TestApi.class, "http://www.baidu.com").buildOkHttp();

        assertThrows(HystrixRuntimeException.class, testApi::call);
        assertThrows(HystrixRuntimeException.class, () -> testApi.hyCall().execute());
    }

    interface TestApi{
        @RequestLine("GET /hello")
        String call();

        @RequestLine("GET /hello")
        HystrixCommand<String> hyCall();
    }
}