package com.xinchen.tool.fegin.hystrix;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.isA;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.*;
import java.util.concurrent.*;
import feign.*;
import feign.Target.HardCodedTarget;
import feign.gson.GsonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;
/**
 * @author xinchen
 * @version 1.0
 * @date 28/08/2020 08:47
 */
public class HystrixBuilderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void defaultMethodReturningHystrixCommand() {
        server.enqueue(new MockResponse().setBody("foo"));
        final TestInterface api = target();

        final HystrixCommand<String> command = api.defaultMethodReturningCommand();

        assertThat(command).isNotNull();
        assertThat(command.execute()).isEqualTo("foo");
    }

    //-----------------------------------------------
    //--- TestInterface 基础功能测试
    //-----------------------------------------------

    @Test
    public void hystrixCommand() {
        server.enqueue(new MockResponse().setBody("\"foo\""));

        final TestInterface api = target();

        final HystrixCommand<String> command = api.command();

        assertThat(command).isNotNull();
        assertThat(command.execute()).isEqualTo("foo");
    }

    @Test
    public void hystrixCommandFallback() {
        // 服务调用失败
        server.enqueue(new MockResponse().setResponseCode(404));

        final TestInterface api = target();

        final HystrixCommand<String> command = api.command();

        assertThat(command).isNotNull();
        // fallback 返回信息
        assertThat(command.execute()).isEqualTo("fallback");
    }

    @Test
    public void hystrixCommandInt() {
        server.enqueue(new MockResponse().setBody("1"));

        final TestInterface api = target();

        final HystrixCommand<Integer> command = api.intCommand();

        assertThat(command).isNotNull();
        assertThat(command.execute()).isEqualTo(1);
    }

    @Test
    public void hystrixCommandIntFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final HystrixCommand<Integer> command = api.intCommand();

        assertThat(command).isNotNull();
        assertThat(command.execute()).isEqualTo(0);
    }

    @Test
    public void hystrixCommandList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));

        final TestInterface api = target();

        final HystrixCommand<List<String>> command = api.listCommand();

        assertThat(command).isNotNull();
        assertThat(command.execute()).containsExactly("foo", "bar");
    }

    @Test
    public void hystrixCommandListFallback() {
        server.enqueue(new MockResponse().setResponseCode(401));

        final TestInterface api = target();

        final HystrixCommand<List<String>> command = api.listCommand();

        assertThat(command).isNotNull();
        assertThat(command.execute()).containsExactly("fallback");
    }

    @Test
    public void rxObservable() {
        // 成功返回
        server.enqueue(new MockResponse().setBody("\"foo\""));

        final TestInterface api = target();

        // 返回可被观察的对象
        final Observable<String> observable = api.observable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        // 注册订阅者
        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        // Blocks 等待被观察对象发布事件
        testSubscriber.awaitTerminalEvent();

        // 到这里已经是执行完毕，查看队列中的结果是否和预期相同
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo("foo");
    }

    @Test
    public void rxObservableFallback() {
        // 服务崩溃，走fallback处理流程
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Observable<String> observable = api.observable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo("fallback");
    }

    @Test
    public void rxObservableInt() {
        server.enqueue(new MockResponse().setBody("1"));

        final TestInterface api = target();

        final Observable<Integer> observable = api.intObservable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(1);
    }

    @Test
    public void rxObservableIntFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Observable<Integer> observable = api.intObservable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(0);
    }

    @Test
    public void rxObservableList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));

        final TestInterface api = target();

        // 可被观察的list对象
        final Observable<List<String>> observable = api.listObservable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        assertThat(testSubscriber.getOnNextEvents().get(0)).containsExactly("foo", "bar");
    }

    @Test
    public void rxObservableListFall() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Observable<List<String>> observable = api.listObservable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        // 走fallback中的处理
        assertThat(testSubscriber.getOnNextEvents().get(0)).containsExactly("fallback");
    }

    @Test
    public void rxObservableListFall_noFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = targetWithoutFallback();

        final Observable<List<String>> observable = api.listObservable();

        assertThat(observable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        // 获取返回结果为empty
        assertThat(testSubscriber.getOnNextEvents()).isEmpty();
        // 在error事件中存在数据
        assertThat(testSubscriber.getOnErrorEvents().get(0))
                .isInstanceOf(HystrixRuntimeException.class)
                .hasMessage("TestInterface#listObservable() failed and no fallback available.");
    }

    @Test
    public void rxSingle() {
        server.enqueue(new MockResponse().setBody("\"foo\""));

        final TestInterface api = target();

        // Single 不能发送onComplete notification
        // 数据与通知只能二选一发射
        final Single<String> single = api.single();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo("foo");
    }

    @Test
    public void rxSingleFallback() {
        server.enqueue(new MockResponse().setResponseCode(302));

        final TestInterface api = target();

        final Single<String> single = api.single();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        // fallback返回
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo("fallback");
    }

    @Test
    public void rxSingleInt() {
        server.enqueue(new MockResponse().setBody("1"));

        final TestInterface api = target();

        final Single<Integer> single = api.intSingle();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(1);
    }

    @Test
    public void rxSingleIntFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Single<Integer> single = api.intSingle();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        Assertions.assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(0);
    }

    @Test
    public void rxSingleList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));

        final TestInterface api = target();

        final Single<List<String>> single = api.listSingle();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        assertThat(testSubscriber.getOnNextEvents().get(0)).containsExactly("foo", "bar");
    }

    @Test
    public void rxSingleListFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Single<List<String>> single = api.listSingle();

        assertThat(single).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        single.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        assertThat(testSubscriber.getOnNextEvents().get(0)).containsExactly("fallback");
    }

    @Test
    public void completableFutureEmptyBody() throws InterruptedException, ExecutionException, TimeoutException {
        server.enqueue(new MockResponse());

        final TestInterface api = target();

        final CompletableFuture<String> completable = api.completableFuture();

        assertThat(completable).isNotNull();

        assertThat(completable.get(5, TimeUnit.SECONDS)).isEqualTo(null);
    }

    @Test
    public void completableFutureWithBody() throws InterruptedException, ExecutionException, TimeoutException {
        server.enqueue(new MockResponse().setBody("foo"));

        final TestInterface api = target();

        final CompletableFuture<String> completable = api.completableFuture();

        assertThat(completable).isNotNull();

        assertThat(completable.get(5, TimeUnit.SECONDS)).isEqualTo("foo");
    }

    @Test
    public void completableFutureFailWithoutFallback() throws TimeoutException, InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target(TestInterface.class, "http://localhost:" + server.getPort());

        final CompletableFuture<String> completable = api.completableFuture();

        assertThat(completable).isNotNull();

        try {
            completable.get(5, TimeUnit.SECONDS);
        } catch (final ExecutionException e) {
            assertThat(e).hasCauseInstanceOf(HystrixRuntimeException.class);
        }
    }

    @Test
    public void completableFutureFallback() throws InterruptedException, ExecutionException, TimeoutException {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final CompletableFuture<String> completable = api.completableFuture();

        assertThat(completable).isNotNull();

        assertThat(completable.get(5, TimeUnit.SECONDS)).isEqualTo("fallback");
    }

    @Test
    public void rxCompletableEmptyBody() {
        server.enqueue(new MockResponse());

        final TestInterface api = target();

        final Completable completable = api.completable();

        assertThat(completable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        completable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        // The number of onCompleted() calls.
        // 这里应该是为1次调用
        testSubscriber.assertCompleted();

        // onErrorEvents
        testSubscriber.assertNoErrors();
    }

    @Test
    public void rxCompletableWithBody() {
        server.enqueue(new MockResponse().setBody("{\"foo\":\"\"}"));

        final TestInterface api = target();

        final Completable completable = api.completable();

        assertThat(completable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        completable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
    }

    @Test
    public void rxCompletableFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final Completable completable = api.completable();

        assertThat(completable).isNotNull();
        assertThat(server.getRequestCount()).isEqualTo(0);

        final TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        completable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertCompleted();
    }

    @Test
    public void plainString() {
        server.enqueue(new MockResponse().setBody("\"foo\""));

        final TestInterface api = target();

        final String string = api.get();

        assertThat(string).isEqualTo("foo");
    }

    @Test
    public void plainStringFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final String string = api.get();

        assertThat(string).isEqualTo("fallback");
    }

    @Test
    public void plainList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));

        final TestInterface api = target();

        final List<String> list = api.getList();

        assertThat(list).isNotNull().containsExactly("foo", "bar");
    }

    @Test
    public void plainListFallback() {
        server.enqueue(new MockResponse().setResponseCode(500));

        final TestInterface api = target();

        final List<String> list = api.getList();

        assertThat(list).isNotNull().containsExactly("fallback");
    }

    @Test
    public void equalsHashCodeAndToStringWork() {
        // 映射target
        final Target<TestInterface> t1 = new HardCodedTarget<>(TestInterface.class, "http://localhost:8080");
        final Target<TestInterface> t2 = new HardCodedTarget<>(TestInterface.class, "http://localhost:8888");
        final Target<OtherTestInterface> t3 = new HardCodedTarget<>(OtherTestInterface.class, "http://localhost:8080");

        final TestInterface i1 = target(t1);
        final TestInterface i2 = target(t1);
        final TestInterface i3 = target(t2);

        final OtherTestInterface i4 = target(t3);

        assertThat(i1)
                .isEqualTo(i2)
                .isNotEqualTo(i3)
                .isNotEqualTo(i4);

        assertThat(i1.hashCode())
                .isEqualTo(i2.hashCode())
                .isNotEqualTo(i3.hashCode())
                .isNotEqualTo(i4.hashCode());

        assertThat(i1.toString())
                .isEqualTo(i2.toString())
                .isNotEqualTo(i3.toString())
                .isNotEqualTo(i4.toString());

        assertThat(t1)
                .isNotEqualTo(i1);

        assertThat(t1.hashCode())
                .isEqualTo(i1.hashCode());

        assertThat(t1.toString())
                .isEqualTo(i1.toString());
    }

    //-----------------------------------------------
    //--- GitHub 基础功能测试
    //-----------------------------------------------


    @Test
    public void fallbacksApplyOnError() {
        server.enqueue(new MockResponse().setResponseCode(403));

        final GitHub fallback = (owner, repo) -> {
            if (owner.equals("Netflix") && repo.equals("feign")) {
                return Arrays.asList("stuarthendren"); // inspired this approach!
            } else {
                return Collections.emptyList();
            }
        };

        final GitHub api = target(GitHub.class, "http://localhost:" + server.getPort(), fallback);

        final List<String> result = api.contributors("Netflix", "feign");

        assertThat(result).containsExactly("stuarthendren");
    }

    @Test
    public void errorInFallbackHasExpectedBehavior() {
        thrown.expect(HystrixRuntimeException.class);
        thrown.expectMessage("GitHub#contributors(String,String) failed and fallback failed.");
        thrown.expectCause(
                isA(FeignException.class)); // as opposed to RuntimeException (from the fallback)

        server.enqueue(new MockResponse().setResponseCode(500));

        // 这里直接调用fallback抛出异常，导致fallback失败
        // 这里的异常抛出去后被处理为FeignException
        final GitHub fallback = (owner, repo) -> {
            throw new RuntimeException("oops");
        };

        final GitHub api = target(GitHub.class, "http://localhost:" + server.getPort(), fallback);

        api.contributors("Netflix", "feign");
    }

    @Test
    public void hystrixRuntimeExceptionPropagatesOnException() {
        thrown.expect(HystrixRuntimeException.class);
        // 这里失败了，但是没有添加fallback 去进行处理
        thrown.expectMessage("GitHub#contributors(String,String) failed and no fallback available.");
        thrown.expectCause(isA(FeignException.class));

        server.enqueue(new MockResponse().setResponseCode(500));

        final GitHub api = target(GitHub.class, "http://localhost:" + server.getPort());

        api.contributors("Netflix", "feign");
    }


    //-----------------------------------------------
    //---内部方法
    //-----------------------------------------------


    protected TestInterface target() {
        return HystrixFeign.builder()
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort(),
                        new FallbackTestInterface());
    }

    protected <E> E target(Class<E> api, String url) {
        return HystrixFeign.builder()
                .target(api, url);
    }

    protected <E> E target(Target<E> api) {
        return HystrixFeign.builder().target(api);
    }

    protected <E> E target(Class<E> api, String url, E fallback) {
        return HystrixFeign.builder()
                .target(api, url, fallback);
    }

    protected TestInterface targetWithoutFallback() {
        return HystrixFeign.builder()
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }

    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<String> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    interface GitHubHystrix {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        HystrixCommand<List<String>> contributorsHystrixCommand(@Param("owner") String owner,
                                                                @Param("repo") String repo);
    }

    interface OtherTestInterface {
        @RequestLine("GET /")
        @Headers("Accept: application/json")
        HystrixCommand<List<String>> listCommand();
    }

    interface TestInterface {

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        HystrixCommand<List<String>> listCommand();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        HystrixCommand<String> command();

        default HystrixCommand<String> defaultMethodReturningCommand() {
            return command();
        }

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        HystrixCommand<Integer> intCommand();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Observable<List<String>> listObservable();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Observable<String> observable();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Single<Integer> intSingle();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Single<List<String>> listSingle();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Single<String> single();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Observable<Integer> intObservable();


        @RequestLine("GET /")
        @Headers("Accept: application/json")
        String get();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        List<String> getList();

        @RequestLine("GET /")
        Completable completable();

        @RequestLine("GET /")
        CompletableFuture<String> completableFuture();
    }

    class FallbackTestInterface implements TestInterface {
        @Override
        public HystrixCommand<String> command() {
            return new HystrixCommand<String>(HystrixCommandGroupKey.Factory.asKey("Test")) {
                @Override
                protected String run() throws Exception {
                    return "fallback";
                }
            };
        }

        @Override
        public HystrixCommand<List<String>> listCommand() {
            return new HystrixCommand<List<String>>(HystrixCommandGroupKey.Factory.asKey("Test")) {
                @Override
                protected List<String> run() throws Exception {
                    final List<String> fallbackResult = new ArrayList<>();
                    fallbackResult.add("fallback");
                    return fallbackResult;
                }
            };
        }

        @Override
        public HystrixCommand<Integer> intCommand() {
            return new HystrixCommand<Integer>(HystrixCommandGroupKey.Factory.asKey("Test")) {
                @Override
                protected Integer run() throws Exception {
                    return 0;
                }
            };
        }

        @Override
        public Observable<List<String>> listObservable() {
            final List<String> fallbackResult = new ArrayList<>();
            fallbackResult.add("fallback");
            return Observable.just(fallbackResult);
        }

        @Override
        public Observable<String> observable() {
            return Observable.just("fallback");
        }

        @Override
        public Single<Integer> intSingle() {
            return Single.just(0);
        }

        @Override
        public Single<List<String>> listSingle() {
            final List<String> fallbackResult = new ArrayList<>();
            fallbackResult.add("fallback");
            return Single.just(fallbackResult);
        }

        @Override
        public Single<String> single() {
            return Single.just("fallback");
        }

        @Override
        public Observable<Integer> intObservable() {
            return Observable.just(0);
        }

        @Override
        public String get() {
            return "fallback";
        }

        @Override
        public List<String> getList() {
            final List<String> fallbackResult = new ArrayList<>();
            fallbackResult.add("fallback");
            return fallbackResult;
        }

        @Override
        public Completable completable() {
            // 标记结束
            return Completable.complete();
        }

        @Override
        public CompletableFuture<String> completableFuture() {
            return CompletableFuture.completedFuture("fallback");
        }
    }
}
