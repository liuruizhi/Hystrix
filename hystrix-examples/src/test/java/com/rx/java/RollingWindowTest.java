package com.rx.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

import java.util.concurrent.TimeUnit;

/**
 * RollingWindowTest
 *
 * @author liuruizhi
 * @Date 2021/6/30
 **/
public class RollingWindowTest {
    private static final Logger logger = LoggerFactory.getLogger(RollingWindowTest.class);

    public static final Func2<Integer, Integer, Integer> INTEGER_SUM =
            (integer, integer2) -> integer + integer2;

    public static final Func1<Observable<Integer>, Observable<Integer>> WINDOW_SUM =
            window -> window.scan(0, INTEGER_SUM).skip(3);

    public static final Func1<Observable<Integer>, Observable<Integer>> INNER_BUCKET_SUM =
            integerObservable -> integerObservable.reduce(0, INTEGER_SUM);

    public static void main(String[] args) throws InterruptedException {
        PublishSubject<Integer> publishSubject = PublishSubject.create();
        SerializedSubject<Integer, Integer> serializedSubject = publishSubject.toSerialized();

        serializedSubject
                .window(5, TimeUnit.SECONDS) // 5秒作为一个基本块
                .flatMap(INNER_BUCKET_SUM)           // 基本块内数据求和
                .window(3, 1)              // 3个块作为一个窗口，滚动布数为1
                .flatMap(WINDOW_SUM)                 // 窗口数据求和
                .subscribe((Integer integer) ->
                        System.out.println(String.format("[%s] call ...... %d",
                                Thread.currentThread().getName(), integer)));

        // 缓慢发送数据，观察效果
        for (int i=0; i<100; ++i) {
            if (i < 30) {
                serializedSubject.onNext(1);
            } else {
                serializedSubject.onNext(2);
            }
            Thread.sleep(1000);
        }

        Thread.sleep(10000);
    }
}
