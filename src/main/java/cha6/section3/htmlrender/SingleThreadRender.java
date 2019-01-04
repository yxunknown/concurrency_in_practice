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
import java.util.ArrayList;
import java.util.List;

import static cha6.section3.htmlrender.RenderHelper.renderImage;
import static cha6.section3.htmlrender.RenderHelper.renderText;
import static cha6.section3.htmlrender.RenderHelper.scanForImageInfo;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 10:11
 */

public class SingleThreadRender {
    public void renderPage(CharSequence source) {
        try {
            renderText(source);
            List<ImageData> imageDatas = new ArrayList<>();
            for (ImageInfo info : scanForImageInfo(source)) {
                // downloadImage是一个耗时操作
                // 在等待downloadImage完成期间，cpu处于空闲状态
                imageDatas.add(info.downloadImage());
            }
            for (ImageData data : imageDatas) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
