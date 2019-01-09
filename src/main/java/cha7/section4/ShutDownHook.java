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

package cha7.section4;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-09 16:39
 */

public class ShutDownHook {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // do something before jvm shutdown
                System.out.println("JVM will be shutdown");
            }
        });
        // 正常的关闭JVM
        System.exit(0);
    }
}
