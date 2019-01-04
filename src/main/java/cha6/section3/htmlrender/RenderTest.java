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

package cha6.section3.htmlrender;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 10:13
 */

public class RenderTest {
    public static void main(String[] args) {
//        testSingleThreadRender(); // 9.569s
//        testFutureRender(); // 9.007s
//        testFutureRenderBetter(); // 3.303s
        testCompletionServiceRender(); // 3.072s
    }

    private static void testSingleThreadRender() {
        SingleThreadRender singleThreadRender = new SingleThreadRender();
        CharSequence source = "html source";
        long start = System.currentTimeMillis();
        singleThreadRender.renderPage(source);
        long end = System.currentTimeMillis();
        System.out.println("render complete in " + (end - start) / 1000.0);
    }

    private static void testFutureRender() {
        FutureRender futureRender = new FutureRender();
        CharSequence source = "html source";
        long start = System.currentTimeMillis();
        futureRender.renderPage(source);
        long end = System.currentTimeMillis();
        System.out.println("render complete in " + (end - start) / 1000.0);
    }

    private static void testFutureRenderBetter() {
        FutureRender futureRender = new FutureRender();
        CharSequence source = "html source";
        long start = System.currentTimeMillis();
        futureRender.renderBetter(source);
        long end = System.currentTimeMillis();
        System.out.println("render complete in " + (end - start) / 1000.0);
    }

    private static void testCompletionServiceRender() {
        CompletionServiceRender render = new CompletionServiceRender();
        CharSequence source = "html source";
        long start = System.currentTimeMillis();
        render.renderPage(source);
        long end = System.currentTimeMillis();
        System.out.println("render complete in " + (end - start) / 1000.0);
//        System.exit(0);
    }
}
