package com.xinchen.tool.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;


/**
 * @author xinchen
 * @version 1.0
 * @date 30/07/2020 15:27
 */
public class HelloWorld {
    public static void main(String[] args) {
        Integer[] arr = new Integer[]{1,2,3,4,5};

        Flowable.just(1,2,3,4,5).map((s) -> "Hello " + s).subscribe(System.out::println).dispose();
        Flowable.fromArray(arr).flatMap((x)-> Flowable.fromArray("Hello "+x)).subscribe(System.out::println).dispose();
        //        Hello 1
        //        Hello 2
        //        Hello 3
        //        Hello 4
        //        Hello 5

        Observable.just(1,2,3,4,5).map((s) -> "Hello " + s).subscribe(System.out::println).dispose();
        Observable.fromArray(arr).flatMap((x)->Observable.fromArray("Hello "+x)).subscribe(System.out::println).dispose();
        //        Hello 1
        //        Hello 2
        //        Hello 3
        //        Hello 4
        //        Hello 5

    }
}
