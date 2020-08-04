package com.xinchen.tool.fegin.spring;

import java.util.ArrayList;
import java.util.Collection;

import feign.DeclarativeContract;
import feign.MethodMetadata;
import feign.Request;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * 针对spring的注解进行解析
 *
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 16:29
 */
public class SpringContract extends DeclarativeContract {
    static final String ACCEPT = "Accept";
    static final String CONTENT_TYPE = "Content-Type";

    public SpringContract() {
        // handle Type annotation
        registerClassAnnotation(RequestMapping.class, (requestMapping, data) -> {
            // handle url
            appendMappings(data, requestMapping.value());

            // handle method
            if (requestMapping.method().length==1){
                data.template().method(Request.HttpMethod.valueOf(requestMapping.method()[0].name()));
            }

            // handle produces
            handleProducesAnnotation(data,requestMapping.produces());

            // handle consumes
            handleConsumesAnnotation(data,requestMapping.consumes());
        });

        // handle Method annotation
        registerMethodAnnotation(RequestMapping.class,(requestMapping,data)->{
            // handle url
            String[] mappings = requestMapping.value();
            appendMappings(data, mappings);

            // handle method
            if (requestMapping.method().length==1){
                data.template().method(Request.HttpMethod.valueOf(requestMapping.method()[0].name()));
            }
        });

        // handle
        registerMethodAnnotation(GetMapping.class, (mapping, data) -> {
            appendMappings(data, mapping.value());
            data.template().method(Request.HttpMethod.GET);
            handleProducesAnnotation(data, mapping.produces());
            handleConsumesAnnotation(data, mapping.consumes());
        });

        registerMethodAnnotation(PostMapping.class, (mapping, data) -> {
            appendMappings(data, mapping.value());
            data.template().method(Request.HttpMethod.POST);
            handleProducesAnnotation(data, mapping.produces());
            handleConsumesAnnotation(data, mapping.consumes());
        });

        registerMethodAnnotation(PutMapping.class, (mapping, data) -> {
            appendMappings(data, mapping.value());
            data.template().method(Request.HttpMethod.PUT);
            handleProducesAnnotation(data, mapping.produces());
            handleConsumesAnnotation(data, mapping.consumes());
        });

        registerMethodAnnotation(DeleteMapping.class, (mapping, data) -> {
            appendMappings(data, mapping.value());
            data.template().method(Request.HttpMethod.DELETE);
            handleProducesAnnotation(data, mapping.produces());
            handleConsumesAnnotation(data, mapping.consumes());
        });

        registerMethodAnnotation(PatchMapping.class, (mapping, data) -> {
            appendMappings(data, mapping.value());
            data.template().method(Request.HttpMethod.PATCH);
            handleProducesAnnotation(data, mapping.produces());
            handleConsumesAnnotation(data, mapping.consumes());
        });


        // handle response
        registerMethodAnnotation(ResponseBody.class, (body, data) -> {
            // 写死返回json
            handleConsumesAnnotation(data, "application/json");
        });

        registerMethodAnnotation(ExceptionHandler.class, (ann, data) -> {
            data.ignoreMethod();
        });

        registerParameterAnnotation(PathVariable.class, (parameterAnnotation, data, paramIndex) -> {
            String name = parameterAnnotation.value();
            nameParam(data, name, paramIndex);
        });

        // handle request
        registerParameterAnnotation(RequestBody.class, (body, data, paramIndex) -> {
            handleProducesAnnotation(data, "application/json");
        });

        registerParameterAnnotation(RequestParam.class, (parameterAnnotation, data, paramIndex) -> {
            String name = RequestParam.class.cast(parameterAnnotation).value();
            Collection<String> query = addTemplatedParam(data.template().queries().get(name), name);
            data.template().query(name, query);
            nameParam(data, name, paramIndex);
        });


    }


    /**
     * 处理{@link RequestMapping#value()} ()} 请求路径地址
     *
     * @param data feign MethodMetadata
     * @param mappings 请求url路径
     */
    private void appendMappings(MethodMetadata data, String[] mappings) {
        for (int i = 0; i < mappings.length; i++) {
            String methodAnnotationValue = mappings[i];
            // 拼接请求url
            if (!methodAnnotationValue.startsWith("/") && !data.template().url().endsWith("/")) {
                methodAnnotationValue = "/" + methodAnnotationValue;
            }
            if (data.template().url().endsWith("/") && methodAnnotationValue.startsWith("/")) {
                methodAnnotationValue = methodAnnotationValue.substring(1);
            }

            data.template().uri(data.template().url() + methodAnnotationValue);
        }
    }

    /**
     * 处理{@link RequestMapping#produces()} 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；
     *
     * 对应http协议中的 Accept
     *
     * <pre class="code">
     * produces = "text/plain"
     * produces = {"text/plain", "application/*"}
     * produces = MediaType.TEXT_PLAIN_VALUE
     * produces = "text/plain;charset=UTF-8"
     * </pre>
     *
     * @param data feign MethodMetadata
     * @param produces produces
     */
    private void handleProducesAnnotation(MethodMetadata data, String... produces) {
        if (produces.length == 0) {
            return;
        }
        // 清理之前的header Accept
        data.template().removeHeader(ACCEPT);

        data.template().header(ACCEPT, produces[0]);
    }

    /**
     * 处理{@link RequestMapping#consumes()} 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
     *
     * 对应http协议中的 Content-Type
     *
     * <pre class="code">
     * consumes = "text/plain"
     * consumes = {"text/plain", "application/*"}
     * consumes = MediaType.TEXT_PLAIN_VALUE
     * </pre>
     *
     * @param data feign MethodMetadata
     * @param consumes consumes
     */
    private void handleConsumesAnnotation(MethodMetadata data, String... consumes) {
        if (consumes.length == 0) {
            return;
        }
        // 清理之前的header Content-Type
        data.template().removeHeader(CONTENT_TYPE);

        data.template().header(CONTENT_TYPE, consumes[0]);
    }

    protected Collection<String> addTemplatedParam(Collection<String> possiblyNull, String name) {
        if (possiblyNull == null) {
            possiblyNull = new ArrayList<String>();
        }
        possiblyNull.add(String.format("{%s}", name));
        return possiblyNull;
    }
}
