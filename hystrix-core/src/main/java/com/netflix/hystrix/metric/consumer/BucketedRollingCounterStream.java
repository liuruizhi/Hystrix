/**
 * Copyright 2015 Netflix, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.metric.consumer;

import com.netflix.hystrix.metric.HystrixEvent;
import com.netflix.hystrix.metric.HystrixEventStream;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Refinement of {@link BucketedCounterStream} which reduces numBuckets at a time.
 *
 * @param <Event> type of raw data that needs to get summarized into a bucket
 * @param <Bucket> type of data contained in each bucket
 * @param <Output> type of data emitted to stream subscribers (often is the same as A but does not have to be)
 */
public abstract class BucketedRollingCounterStream<Event extends HystrixEvent, Bucket, Output> extends BucketedCounterStream<Event, Bucket, Output> {
    private Observable<Output> sourceStream;
    private final AtomicBoolean isSourceCurrentlySubscribed = new AtomicBoolean(false);

    /**
     *
     * @param stream
     * @param numBuckets
     * @param bucketSizeInMs
     * @param appendRawEventToBucket bucket内部统计函数，其功能是起始值Bucket加上Event后，输出Bucket类型值，对数据的处理具有累积的效果；
     * @param reduceBucket 和 appendRawEventToBucket 类似，用于 window 统计。
     */
    protected BucketedRollingCounterStream(HystrixEventStream<Event> stream, final int numBuckets, int bucketSizeInMs,
                                           final Func2<Bucket, Event, Bucket> appendRawEventToBucket,
                                           final Func2<Output, Bucket, Output> reduceBucket) {
        super(stream, numBuckets, bucketSizeInMs, appendRawEventToBucket);
        Func1<Observable<Bucket>, Observable<Output>> reduceWindowToSummary = new Func1<Observable<Bucket>, Observable<Output>>() {
            @Override
            public Observable<Output> call(Observable<Bucket> window) {
                return window.scan(getEmptyOutputValue(), reduceBucket).skip(numBuckets);
            }
        };
        //stream broken up into buckets
        this.sourceStream = bucketedStream
                //emit overlapping windows of buckets
                .window(numBuckets, 1)
                //convert a window of bucket-summaries into a single summary
                .flatMap(reduceWindowToSummary)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        isSourceCurrentlySubscribed.set(true);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        isSourceCurrentlySubscribed.set(false);
                    }
                })
                .share()                        //multiple subscribers should get same data
                .onBackpressureDrop();          //if there are slow consumers, data should not buffer
    }

    @Override
    public Observable<Output> observe() {
        return sourceStream;
    }

    /* package-private */ boolean isSourceCurrentlySubscribed() {
        return isSourceCurrentlySubscribed.get();
    }
}
