package com.xinchen.tool.fegin.hystrix;

import com.netflix.hystrix.HystrixCommand;
import feign.Capability;
import feign.Contract;
import feign.InvocationHandlerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 允许Feign接口返回HystrixCommand或rx.Observable或rx.Single对象。
 *
 * 也可以通过调用{@link HystrixCommand#execute()} 用断路器装饰普通的Feign方法
 *
 * @author xinchen
 * @version 1.0
 * @date 25/08/2020 17:07
 */
public class HystrixCapability implements Capability {

    private SetterFactory setterFactory = new SetterFactory.Default();
    private final Map<Class, Object> fallbacks = new HashMap<>();

    /**
     * Allows you to override hystrix properties such as thread pools and command keys.
     */
    public HystrixCapability setterFactory(SetterFactory setterFactory) {
        this.setterFactory = setterFactory;
        return this;
    }

    @Override
    public Contract enrich(Contract contract) {
        return new HystrixDelegatingContract(contract);
    }

    @Override
    public InvocationHandlerFactory enrich(InvocationHandlerFactory invocationHandlerFactory) {
        return (target, dispatch) -> new HystrixInvocationHandler(target, dispatch, setterFactory,
                fallbacks.containsKey(target.type())
                        ? new FallbackFactory.Default<>(fallbacks.get(target.type()))
                        : null);
    }

    public <E> Capability fallback(Class<E> api, E fallback) {
        fallbacks.put(api, fallback);

        return this;
    }


}
