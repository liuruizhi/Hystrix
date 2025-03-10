/**
 * Copyright 2012 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.strategy.eventnotifier;

import java.util.List;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.HystrixPlugins;

/**
 * Abstract EventNotifier that allows receiving notifications for different events with default implementations.
 * <p>
 * See {@link HystrixPlugins} or the Hystrix GitHub Wiki for information on configuring plugins: <a
 * href="https://github.com/Netflix/Hystrix/wiki/Plugins">https://github.com/Netflix/Hystrix/wiki/Plugins</a>.
 * <p>
 * <b>Note on thread-safety and performance</b>
 * <p>
 * A single implementation of this class will be used globally so methods on this class will be invoked concurrently from multiple threads so all functionality must be thread-safe.
 * <p>
 * Methods are also invoked synchronously and will add to execution time of the commands so all behavior should be fast. If anything time-consuming is to be done it should be spawned asynchronously
 * onto separate worker threads.
 * 这个notifier是同步调用的，因此里头方法的实现不能太耗时，不然则会阻塞，如果方法太耗时则需要考虑异步到其他线程。
 */
public abstract class HystrixEventNotifier {

    /**
     * Called for every event fired.
     * <p>
     * <b>Default Implementation: </b> Does nothing
     * 
     * @param eventType event type
     * @param key event key
     *
     * 空实现：当任意事件被触发时，均执行此方法
     */
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        // do nothing
    }

    /**
     * Called after a command is executed using thread isolation.
     * <p>
     * Will not get called if a command is rejected, short-circuited etc.
     * <p>
     * <b>Default Implementation: </b> Does nothing
     *
     * 空实现：当使用线程隔离方式，触发事件时执行此方法
     * 注意：如果被拒绝rejected（比如断路器全开了）、或者短路short-circuited了，那么此方法是不会被调用的
     * 
     * @param key
     *            {@link HystrixCommandKey} of command instance.
     * @param isolationStrategy
     *            {@link ExecutionIsolationStrategy} the isolation strategy used by the command when executed
     * @param duration
     *            time in milliseconds of executing <code>run()</code> method
     * @param eventsDuringExecution
     *            {@code List<HystrixEventType>} of events occurred during execution.
     */
    public void markCommandExecution(HystrixCommandKey key, ExecutionIsolationStrategy isolationStrategy, int duration, List<HystrixEventType> eventsDuringExecution) {
        // do nothing
    }

}
