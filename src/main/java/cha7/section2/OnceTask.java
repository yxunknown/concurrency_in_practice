/*
 * Copyright (C) 2019 The concurrency_in_practice Authors
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

package cha7.section2;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-07 16:13
 */

public class OnceTask {

    /**
     * check if all urls in set are valid url string
     * if all urls are valid, return true; otherwise return false
     * @param urls urls set
     * @return if all urls are valid: return true
     *         otherwise: return false
     */
    public static boolean checkUrl(Set<String> urls, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService cachedExec = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            200,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("Once-task-poll-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
        final AtomicBoolean result = new AtomicBoolean(true);
        try {
            for (String url : urls) {
                cachedExec.execute(() -> {
                    String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
                    System.out.println(Thread.currentThread().getName() + " checking " + url);
                    if (!Pattern.matches(regex, url)) {
                        result.set(false);
                    }
                });
            }
        } finally {
            // 关闭executor
            cachedExec.shutdown();
            cachedExec.awaitTermination(timeout, unit);
        }
        return result.get();
    }

    public static void main(String[] args) throws InterruptedException {
        Set<String> urls = new HashSet<>();
        urls.add("http://www.baidu.com");
        urls.add("http://132.232.34.232:8000/wapit");
        urls.add("https://google.com/p?key=we");
        urls.add("htts://google.com/p?key=we");
        boolean allUrlsValid = checkUrl(urls, 2000, TimeUnit.MILLISECONDS);
        System.out.println(allUrlsValid);
    }
}
