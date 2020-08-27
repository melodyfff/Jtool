package com.xinchen.tool.fegin.hystrix;

import com.netflix.hystrix.HystrixCommand;
import feign.Contract;
import feign.MethodMetadata;
import rx.Completable;
import rx.Observable;
import rx.Single;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static feign.Util.resolveLastTypeParameter;

/**
 * This special cases methods that return {@link HystrixCommand}, {@link Observable}, or
 * {@link Single} so that they are decoded properly.
 *
 * <p>
 * For example, {@literal HystrixCommand<Foo>} and {@literal Observable<Foo>} will decode
 * {@code Foo}.
 *
 * @author xinchen
 * @version 1.0
 * @date 25/08/2020 17:24
 */
public class HystrixDelegatingContract implements Contract {

    private final Contract delegate;

    HystrixDelegatingContract(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        List<MethodMetadata> metadatas = this.delegate.parseAndValidateMetadata(targetType);

        for (MethodMetadata metadata : metadatas) {
            Type type = metadata.returnType();

            if (type instanceof ParameterizedType
                    && ((ParameterizedType) type).getRawType().equals(HystrixCommand.class)) {
                Type actualType = resolveLastTypeParameter(type, HystrixCommand.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType
                    && ((ParameterizedType) type).getRawType().equals(Observable.class)) {
                Type actualType = resolveLastTypeParameter(type, Observable.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType
                    && ((ParameterizedType) type).getRawType().equals(Single.class)) {
                Type actualType = resolveLastTypeParameter(type, Single.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType
                    && ((ParameterizedType) type).getRawType().equals(Completable.class)) {
                metadata.returnType(void.class);
            } else if (type instanceof ParameterizedType
                    && ((ParameterizedType) type).getRawType().equals(CompletableFuture.class)) {
                metadata.returnType(resolveLastTypeParameter(type, CompletableFuture.class));
            }
        }


        return metadatas;
    }
}