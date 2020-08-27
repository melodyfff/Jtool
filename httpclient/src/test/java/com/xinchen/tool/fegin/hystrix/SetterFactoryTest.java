package com.xinchen.tool.fegin.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.RequestLine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * @author xinchen
 * @version 1.0
 * @date 26/08/2020 08:59
 */
public class SetterFactoryTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void customSetter() {
        thrown.expect(HystrixRuntimeException.class);
        thrown.expectMessage("POST / failed and no fallback available.");
        server.enqueue(new MockResponse().setResponseCode(500));


        // 自定义SetterFactory设置groupKey和commandKey
        SetterFactory commandKeyIsRequestLine = (target, method) -> {
            String groupKey = target.name();
            String commandKey = method.getAnnotation(RequestLine.class).value();
            return HystrixCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
        };

        TestInterface api = HystrixFeign.builder()
                .setterFactory(commandKeyIsRequestLine)
                .target(TestInterface.class, "http://localhost:" + server.getPort());

        api.invoke();
    }


    interface TestInterface {
        @RequestLine("POST /")
        String invoke();
    }
}