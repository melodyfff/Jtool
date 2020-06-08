package com.xinchen.tool.httptrace.trace;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FrameworkServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * aspect annotation {@link HttpTrace}, and do some business.
 *
 *
 * Aspect Running Sequence:
 * Success      : @Around -> @Before -> 执行方法 ->  @Around -> @After -> @AfterReturning
 * fail(error)  : @Around -> @Before -> 方法报错 ->  @After  -> @AfterThrowing
 *
 *
 * Execution Order:
 * mark {@link Order} ,  Lower values have higher priority
 *
 * @author xinchen
 * @version 1.0
 * @date 08/06/2020 14:22
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class HttpTraceAspect {

    @Pointcut("@annotation(com.xinchen.tool.httptrace.trace.HttpTrace)")
    public void httpTraceCut() {

    }

    @Before("httpTraceCut()")
    public void beforeProcess(JoinPoint joinPoint) {
        final Signature signature = joinPoint.getSignature();
        log.info("[HttpTrace]-[Before] cut: {}", signature);
    }

    @Around("httpTraceCut()")
    public Object aroundProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature signature = joinPoint.getSignature();
        log.info("[HttpTrace]-[Around]-[Start] cut: {}", signature);

        // analysis annotation , resolve attr.
        final HttpTrace annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(HttpTrace.class);
        analysisAnnotation(annotation);

        final Object proceed = joinPoint.proceed();
        log.info("[HttpTrace]-[Around]-[End] cut: {}", signature);
        return proceed;
    }

    @After("httpTraceCut()")
    public void afterProcess(JoinPoint joinPoint) {
        final Signature signature = joinPoint.getSignature();
        log.info("[HttpTrace]-[After] cut: {}", signature);
    }

    @AfterReturning(value = "httpTraceCut()",returning = "result")
    public void afterReturn(JoinPoint joinPoint,Object result){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("[HttpTrace]-[AfterReturn] cut: {} ,result: {}", signature,result);
    }


    /**
     * peek the throw
     *
     * FrameworkServlet will handle it later
     *
     * @see FrameworkServlet#processRequest(HttpServletRequest request, HttpServletResponse response)
     * @param joinPoint joinPoint
     * @param e Exception
     */
    @AfterThrowing(value = "httpTraceCut()",throwing = "e")
    public void afterThrow(JoinPoint joinPoint,Exception e){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("[HttpTrace]-[AfterThrow] cut: {} ,exception : ", signature,e);
    }

    private static void analysisAnnotation(HttpTrace httpTrace){
        log.info("HttpTrace Attributes :{}",httpTrace);
    }

}
