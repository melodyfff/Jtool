package com.xinchen.tool.guice.demo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A simple Guice application
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/4/23 21:34
 */
public class GuiceDemo {
    /*注解部分*/
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Message {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Count {}

    /*模块部分*/
    static class DemoModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Key.get(String.class,Message.class)).toInstance("Hello World.");
        }

        @Provides
        @Count
        static Integer provideCount(){
            return 3;
        }
    }

    static class Greeter {
        private final String message;
        private final int count;

        @Inject
        Greeter(@Message String message,@Count int count) {
            this.message = message;
            this.count = count;
        }

        void sayHello(){
            for (int i = 0; i < count; i++) {
                System.out.println(message);
            }
        }
    }

    public static void main(String[] args) {
        /*
         * Guice.createInjector() takes one or more modules, and returns a new Injector
         * instance. Most applications will call this method exactly once, in their
         * main() method.
         */
        Injector injector = Guice.createInjector(new DemoModule());

        /*
         * Now that we've got the injector, we can build objects.
         */
        Greeter greeter = injector.getInstance(Greeter.class);

        // Prints "hello world" 3 times to the console.
        greeter.sayHello();
    }
}
