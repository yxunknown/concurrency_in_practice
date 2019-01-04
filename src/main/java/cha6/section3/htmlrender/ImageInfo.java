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

import java.time.Instant;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-03 10:00
 */

public class ImageInfo {
    private String url;

    public ImageInfo() {
        this.url = "https://avatars1.githubusercontent.com/u/22553946?s=460&v=4";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageData downloadImage() throws InterruptedException {
        Thread.sleep(2000);
        ImageData imageData = new ImageData();
        imageData.setBase64content(this.url + Instant.now());
        return imageData;
    }
}
