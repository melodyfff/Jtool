package com.xinchen.tool.guice.demo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xinchen
 * @version 1.0
 * @date 18/06/2020 15:47
 */
public class GuiceDemo2 {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface SayService {
    }


    static class Demo2Module extends AbstractModule {


        @Override
        protected void configure() {
            // 默认的实现方式
            bind(Service.class).toInstance(new DefaultServiceImpl());
        }

        @Provides
        @SayService
        static Service provideService() {
            // 使用了@SayService标记的
            return new SPServiceImpl();
        }
    }


    interface Service{
        void say(String message);
    }

    static class DefaultServiceImpl implements Service{
        @Override
        public void say(String message){
            System.out.println("Default : " + message);
        }
    }

    static class SPServiceImpl implements Service{

        @Override
        public void say(String message) {
            System.out.println("SP : "+message);
        }
    }

    static class Hello {
        private final Service service;

        @Inject
        Hello(@SayService Service service) {
            this.service = service;
        }


        void say(String message) {
            service.say(message);
        }
    }

    public static void main(String[] args) {
        final Injector injector = Guice.createInjector(new Demo2Module());
        final Hello hello = injector.getInstance(Hello.class);
        hello.say("Hello World.");
    }
}
