package com.xinchen.tool.fegin.hystrix;

import feign.FeignException;
import feign.RequestLine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xinchen.tool.fegin.MockWebServerAssertions.assertThat;


/**
 * @author xinchen
 * @version 1.0
 * @date 26/08/2020 15:29
 */
public class FallbackFactoryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    interface TestInterface {
        @RequestLine("POST /")
        String invoke();
    }


    @Test
    public void fallbackFactory_example_lambda() {
        // 第一次返回500 第二次返回404
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse().setResponseCode(404));

        TestInterface api = target(cause -> () -> {
            assertThat(cause).isInstanceOf(FeignException.class);
            return ((FeignException) cause).status() == 500 ? "foo" : "bar";
        });



        assertThat(api.invoke()).isEqualTo("foo");
        assertThat(api.invoke()).isEqualTo("bar");
    }


    static class FallbackApiWithCtor implements TestInterface {
        final Throwable cause;

        FallbackApiWithCtor(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public String invoke() {
            return "foo";
        }
    }

    @Test
    public void fallbackFactory_example_ctor() {
        server.enqueue(new MockResponse().setResponseCode(500));

        // method reference
        TestInterface api = target(FallbackApiWithCtor::new);

        assertThat(api.invoke()).isEqualTo("foo");

        server.enqueue(new MockResponse().setResponseCode(500));

        // lambda factory
        api = target(throwable -> new FallbackApiWithCtor(throwable));

        server.enqueue(new MockResponse().setResponseCode(500));

        // old school
        api = target(new FallbackFactory<TestInterface>() {
            @Override
            public TestInterface create(Throwable cause) {
                return new FallbackApiWithCtor(cause);
            }
        });

        assertThat(api.invoke()).isEqualTo("foo");
    }

    // retrofit so people don't have to track 2 classes
    static class FallbackApiRetro implements TestInterface, FallbackFactory<FallbackApiRetro> {

        @Override
        public FallbackApiRetro create(Throwable cause) {
            return new FallbackApiRetro(cause);
        }

        final Throwable cause; // nullable

        public FallbackApiRetro() {
            this(null);
        }

        FallbackApiRetro(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public String invoke() {
            return cause != null ? cause.getMessage() : "foo";
        }
    }

    @Test
    public void fallbackFactory_example_retro() {
        server.enqueue(new MockResponse().setResponseCode(500));

        TestInterface api = target(new FallbackApiRetro());

        assertThat(api.invoke()).isEqualTo(
                "[500 Server Error] during [POST] to [http://localhost:" + server.getPort()
                        + "/] [TestInterface#invoke()]: []");
    }

    @Test
    public void defaultFallbackFactory_delegates() {
        // 委托类fallback
        server.enqueue(new MockResponse().setResponseCode(500));

        TestInterface api = target(new FallbackFactory.Default<>(() -> "foo"));

        assertThat(api.invoke()).isEqualTo("foo");
    }

    @Test
    public void defaultFallbackFactory_doesntLogByDefault() {
        server.enqueue(new MockResponse().setResponseCode(500));

        Logger logger = new Logger("", null) {
            @Override
            public void log(Level level, String msg, Throwable thrown) {
                throw new AssertionError("logged eventhough not FINE level");
            }
        };

        target(new FallbackFactory.Default<>(() -> "foo", logger)).invoke();
    }

    @Test
    public void defaultFallbackFactory_logsAtFineLevel() {
        server.enqueue(new MockResponse().setResponseCode(500));

        AtomicBoolean logged = new AtomicBoolean();
        Logger logger = new Logger("", null) {
            @Override
            public void log(Level level, String msg, Throwable thrown) {
                logged.set(true);

                assertThat(msg)
                        .isEqualTo("fallback due to: [500 Server Error] during [POST] to [http://localhost:"
                                + server.getPort() + "/] [TestInterface#invoke()]: []");
                assertThat(thrown).isInstanceOf(FeignException.class);
            }
        };
        logger.setLevel(Level.FINE);

        target(new FallbackFactory.Default<>(() -> "foo", logger)).invoke();
        assertThat(logged.get()).isTrue();
    }

    /**
     * 主要测试fallback
     * @param factory fallback factory
     * @return TestInterface
     */
    private TestInterface target(FallbackFactory<? extends TestInterface> factory) {
        return HystrixFeign.builder()
                .target(TestInterface.class, "http://localhost:" + server.getPort(), factory);
    }
}