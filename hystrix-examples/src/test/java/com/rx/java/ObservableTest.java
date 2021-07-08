package com.rx.java;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * ObservableTest
 *
 * @author liuruizhi
 * @Date 2021/6/30
 **/
public class ObservableTest {

    public static void main(String[] args) {
        Observable.just("Hello ", "World !").subscribe(new Subscriber<String>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("@@@ call : " + s);
            }
        });
    }
}
