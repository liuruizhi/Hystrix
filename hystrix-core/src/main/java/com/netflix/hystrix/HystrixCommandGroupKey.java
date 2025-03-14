/**
 * Copyright 2012 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;

import com.netflix.hystrix.util.InternMap;

/**
 * 对command进行分组便于统计等
 *
 * A group name for a {@link HystrixCommand}. This is used for grouping together commands such as for reporting, alerting, dashboards or team/library ownership.
 * <p>
 * By default this will be used to define the {@link HystrixThreadPoolKey} unless a separate one is defined.
 * <p>
 * This interface is intended to work natively with Enums so that implementing code can have an enum with the owners that implements this interface.
 * @author HystrixCommandGroupKey
 */
public interface HystrixCommandGroupKey extends HystrixKey {
    class Factory {
        private Factory() {
        }

        // used to intern instances so we don't keep re-creating them millions of times for the same key
        // InternMap内部就是一个ConcurrentHashMap，然后有一个接口ValueConstructor，如下提供了默认实现create
        private static final InternMap<String, HystrixCommandGroupDefault> intern
                = new InternMap<String, HystrixCommandGroupDefault>(
                new InternMap.ValueConstructor<String, HystrixCommandGroupDefault>() {
                    @Override
                    public HystrixCommandGroupDefault create(String key) {
                        return new HystrixCommandGroupDefault(key);
                    }
                });


        /**
         * Retrieve (or create) an interned HystrixCommandGroup instance for a given name.
         *
         * @param name command group name
         * @return HystrixCommandGroup instance that is interned (cached) so a given name will always retrieve the same instance.
         */
        public static HystrixCommandGroupKey asKey(String name) {
           return intern.interned(name);
        }

        /**
         * 类名含有default，但是所有的都是这个，也不存在是不是default一说
         */
        private static class HystrixCommandGroupDefault extends HystrixKey.HystrixKeyDefault implements HystrixCommandGroupKey {
            public HystrixCommandGroupDefault(String name) {
                super(name);
            }
        }

        /**
         * 就是map的大小
         * @return
         */
        /* package-private */ static int getGroupCount() {
            return intern.size();
        }
    }
}