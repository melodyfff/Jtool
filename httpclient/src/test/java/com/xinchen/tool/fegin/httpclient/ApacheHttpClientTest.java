package com.xinchen.tool.fegin.httpclient;

import com.xinchen.tool.fegin.AbstractClientTest;
import feign.Feign;
import feign.jaxrs.JAXRSContract;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 10:37
 */
public class ApacheHttpClientTest extends AbstractClientTest {

    @Override
    public Feign.Builder newBuilder() {
        return Feign.builder().client(new ApacheHttpClient());
    }

    @Test
    public void queryParamsAreRespectedWhenBodyIsEmpty() throws InterruptedException {
        final HttpClient httpClient = HttpClientBuilder.create().build();
        final JaxRsTestInterface testInterface = Feign.builder()
                .contract(new JAXRSContract())
                .client(new ApacheHttpClient(httpClient))
                .target(JaxRsTestInterface.class, "http://localhost:" + server.getPort());

        server.enqueue(new MockResponse().setBody("foo"));
        server.enqueue(new MockResponse().setBody("foo"));

        assertEquals("foo", testInterface.withBody("foo", "bar"));
        final RecordedRequest request1 = server.takeRequest();
        assertEquals("/withBody?foo=foo", request1.getPath());
        assertEquals("bar", request1.getBody().readString(StandardCharsets.UTF_8));

        assertEquals("foo", testInterface.withoutBody("foo"));
        final RecordedRequest request2 = server.takeRequest();
        assertEquals("/withoutBody?foo=foo", request2.getPath());
        assertEquals("", request2.getBody().readString(StandardCharsets.UTF_8));
    }

    @Path("/")
    public interface JaxRsTestInterface {
        @PUT
        @Path("/withBody")
        String withBody(@QueryParam("foo") String foo, String bar);

        @PUT
        @Path("/withoutBody")
        String withoutBody(@QueryParam("foo") String foo);
    }
}