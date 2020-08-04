package com.xinchen.tool.fegin.spring;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;


/**
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 17:09
 */
public class SpringContractTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MockClient mockClient;
    private HealthResource resource;

    @Before
    public void setup() throws IOException {
        mockClient = new MockClient()
                .noContent(HttpMethod.GET, "/health")
                .noContent(HttpMethod.GET, "/health/1")
                .noContent(HttpMethod.GET, "/health/1?deep=true")
                .noContent(HttpMethod.GET, "/health/1?deep=true&dryRun=true")
                .ok(HttpMethod.GET, "/health/generic", "{}");


        // build feign
        resource = Feign.builder()
                .contract(new SpringContract())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.BASIC)
                .client(mockClient)
                .target(new MockTarget<>(HealthResource.class));
    }

    @Test
    public void requestParam() {
        resource.check("1", true);
        mockClient.verifyOne(HttpMethod.GET, "/health/1?deep=true");
    }

    @Test
    public void requestTwoParams() {
        resource.check("1", true, true);
        mockClient.verifyOne(HttpMethod.GET, "/health/1?deep=true&dryRun=true");
    }

    @Test
    public void inheritance() {
        // 继承自GenericResource
        // 发起@ RequestBody DTO input
        final Data data = resource.getData(new Data());
        assertThat(data, notNullValue());

        // 检测之前的请求提交类型中consumes 包含application/json
        final Request request = mockClient.verifyOne(HttpMethod.GET, "/health/generic");
        assertThat(request.headers(), hasEntry(
                "Content-Type", Collections.singletonList("application/json")));
    }

    @Test
    public void composedAnnotation() {
        resource.check("1");
        mockClient.verifyOne(HttpMethod.GET, "/health/1");
    }

    @Test
    public void notAHttpMethod() {
        thrown.expectMessage("is not a method handled by feign");
        try {
            resource.missingResourceExceptionHandler();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}