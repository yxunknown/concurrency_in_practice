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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.List;
import java.util.concurrent.*;

import static cha6.section3.htmlrender.RenderHelper.*;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 14:20
 */

public class CompletionServiceRender {
    private final ExecutorService executor;

    public CompletionServiceRender() {
        this.executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("render-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
    }

    void renderPage(CharSequence source) {
        try {
            List<ImageInfo> imageInfos = scanForImageInfo(source);
            CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
            for (final ImageInfo info : imageInfos) {
                // submit download images task
                completionService.submit(() -> {
                    return info.downloadImage();
                });
            }
            // render text
            renderText(source);
            for (int t = 0, n = imageInfos.size(); t < n; t++) {
                // take是一个消费线程
                // 如果队列中有可消费对象，则立即返回
                // 如果队列为空，则阻塞
                // 该模式实现了某种图片下载完成后可以立即被渲染
                Future<ImageData> f = completionService.take();
                ImageData data = f.get();
                renderImage(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println(e.getCause().getMessage());
        }

    }
}
