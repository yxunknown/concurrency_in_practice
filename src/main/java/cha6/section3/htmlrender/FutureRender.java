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
import static cha6.section3.htmlrender.RenderHelper.scanForImageInfo;
import static cha6.section3.htmlrender.RenderHelper.renderText;
import static cha6.section3.htmlrender.RenderHelper.renderImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 11:06
 */

public class FutureRender {
    private final ExecutorService executorService = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        1000,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder().setNameFormat("render-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    void renderPage(CharSequence source) {
        try {
            final List<ImageInfo> imageInfos = scanForImageInfo(source);
            Callable<List<ImageData>> task = () -> {
                // 提交的任务执行时间还是很长
                // 该渲染器只实现了获取图片和渲染文本的并发
                // 仍然要将所有图片下载完成后才开始渲染图像
                // 这里对性能提升不明显的原因在于，渲染文本的速度远远大于下载图像和渲染图像的速度
                // 因此并行执行所带来的改善只限于减少了渲染文本的时间，而处理图片仍然是单线程串行处理的
                List<ImageData> result = new ArrayList<>();
                for (ImageInfo info : imageInfos) {
                    result.add(info.downloadImage());
                }
                return result;
            };
            Future<List<ImageData>> future = executorService.submit(task);
            renderText(source);
            List<ImageData> imageData = future.get();
            for (ImageData data : imageData) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    /**
     * 该渲染方法改善了如上的渲染方法
     * 但是该方法无法正常退出
     * @param source render source
     */
    void renderBetter(CharSequence source) {
        try {
            final List<ImageInfo> imageInfos = scanForImageInfo(source);
            List<Future<ImageData>> futures = new ArrayList<>();
            for (ImageInfo info : imageInfos) {
                // multiple thread to download image
                Callable<ImageData> task = getTask(info);
                Future<ImageData> future = executorService.submit(task);
                futures.add(future);
            }
            renderText(source);
            // 异步获取图片下载结果
            for (Future<ImageData> future : futures) {
                // 每下载完成一张图像后，便开始渲染
                renderImage(future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    private Callable<ImageData> getTask(ImageInfo info) {
        return () -> {
            return info.downloadImage();
        };
    }
}
