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

import java.util.Arrays;
import java.util.List;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 10:03
 */

public class RenderHelper {

    public static void renderText(CharSequence source) throws InterruptedException {
        Thread.sleep(500);
        System.out.println("Text contents rendering completes");
    }

    public static List<ImageInfo> scanForImageInfo(CharSequence sequence) throws InterruptedException {
        Thread.sleep(1000);
        return Arrays.asList(new ImageInfo(), new ImageInfo(), new ImageInfo(), new ImageInfo());
    }

    public static void renderImage(ImageData data) {
        System.out.println("Render image: " + data.getBase64content());
    }
}
