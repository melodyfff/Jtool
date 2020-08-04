package com.xinchen.tool.fegin;

import feign.Feign.Builder;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.rules.ExpectedException;



/**
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 11:28
 */
public abstract class AbstractClientTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    /**
     * Create a Feign {@link Builder} with a client configured
     */
    public abstract Builder newBuilder();
}
