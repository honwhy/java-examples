# 读取图片元数据也存在有趣的情况

搭建一个普通的 Spring Boot 项目，写一个普通的 Controller 接口，接受图片链接参数，读取该图片的元数据并返回。

## 技术栈

1. Java 8
2. Spring Boot 2.7.18

## Controller 源码

```java
package com.honwhy.examples.image.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class ImageMetadataController {

    @GetMapping("/image/metadata")
    public Map<String, Object> getMetadata(@RequestParam("url") String imageUrl) {
        Map<String, Object> result = new HashMap<>();
        try (InputStream input = openStream(imageUrl)) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                result.put("success", false);
                result.put("message", "无法识别图片格式或图片已损坏");
                return result;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("url", imageUrl);
            data.put("width", image.getWidth());
            data.put("height", image.getHeight());
            data.put("imageType", image.getType());
            data.put("numBands", image.getRaster().getNumBands());
            data.put("colorModel", image.getColorModel().getClass().getName());
            data.put("colorSpaceType", image.getColorModel().getColorSpace().getType());
            data.put("colorSpaceNumComponents", image.getColorModel().getColorSpace().getNumComponents());
            data.put("transparency", image.getTransparency());
            data.put("alphaPremultiplied", image.isAlphaPremultiplied());
            data.put("bitDepth", image.getColorModel().getPixelSize());
            data.put("format", detectFormat(imageUrl, image));

            result.put("success", true);
            result.put("data", data);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "读取图片失败: " + e.getMessage());
        }
        return result;
    }

    private InputStream openStream(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.0.0 Safari/537.36 Edg/151.0.0.0");
        int code = connection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP 请求失败，响应码: " + code);
        }
        return connection.getInputStream();
    }

    private String detectFormat(String imageUrl, BufferedImage image) throws IOException {
        if (imageUrl != null && imageUrl.contains(".")) {
            String ext = imageUrl.substring(imageUrl.lastIndexOf('.') + 1).toLowerCase();
            if (ext.matches("(jpeg|jpg|png|webp|gif|bmp)")) {
                return ext;
            }
        }
        Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
        return readers.hasNext() ? readers.next().getFormatName() : "unknown";
    }
}
```

## 正常情况下的测试效果

测试命令：

```
curl --request GET --url 'http://localhost:8081/image/metadata?url=https%3A%2F%2Fkkimgs.yisou.com%2Fims%3Fkt%3Durl%26at%3Dori%26key%3DaHR0cDovL2ltZy56Y29vbC5jbi9jb21tdW5pdHkvMDM4MTg0ZjU3ODgzY2IwMDAwMDAxOGMxYjFhOWNkNS5naWY%3D%26sign%3Dyx%3AZMcn1EoLPVaM3j_xjPfusHaq4LU%3D%26tv%3D0_0'
```

结果：

```json
{
  "data": {
    "numBands": 1,
    "colorSpaceNumComponents": 3,
    "alphaPremultiplied": false,
    "colorSpaceType": 5,
    "transparency": 2,
    "bitDepth": 8,
    "width": 658,
    "format": "unknown",
    "colorModel": "java.awt.image.IndexColorModel",
    "imageType": 13,
    "url": "https://kkimgs.yisou.com/ims?kt=url&at=ori&key=aHR0cDovL2ltZy56Y29vbC5jbi9jb21tdW5pdHkvMDM4MTg0ZjU3ODgzY2IwMDAwMDAxOGMxYjFhOWNkNS5naWY=&sign=yx:ZMcn1EoLPVaM3j_xjPfusHaq4LU=&tv=0_0",
    "height": 494
  },
  "success": true
}
```

原始图片是：

![](https://fastly.jsdelivr.net/gh/bucketio/img1@main/2026/08/13/1786638303653-276760e1-8eb3-42bc-95c5-06d17cf57113.gif)

按照 AI 的说法，Java 原生 ImageIO 只支持 jpeg/jpg/png/gif/bmp，不支持 webp；如需 webp 需加第三方插件（如 `com.github.gotson:webp-imageio` 或 TwelveMonkeys）。

## 正常情况下的 JPG 示例

比如下面这张 JPG 图片，可以正常读取到它的元数据：

![](https://fastly.jsdelivr.net/gh/bucketio/img11@main/2026/08/13/1786638753975-e4771509-5545-42fb-ae5a-6f866368bf8b.png)

```json
{
  "data": {
    "numBands": 3,
    "colorSpaceNumComponents": 3,
    "alphaPremultiplied": false,
    "colorSpaceType": 5,
    "transparency": 1,
    "bitDepth": 24,
    "width": 1024,
    "format": "jpg",
    "colorModel": "java.awt.image.ComponentColorModel",
    "imageType": 5,
    "url": "https://gw.alicdn.com/imgextra/i3/O1CN01WQ3o7J1mj0uiYtkJX_!!6000000004989-0-tps-1024-1024.jpg",
    "height": 1024
  },
  "success": true
}
```

## WebP 的问题

下面这张图片是 webp 格式的，在调用 `ImageIO.read` 方法时无法识别：

![](https://kkimgs.yisou.com/ims?kt=url&at=ori&key=aHR0cHM6Ly93d3cubG9uZy1waG90by5jb20vdXBsb2Fkcy8yMDIwLzA4MTEvNmFlMGM0OGIxZDllN2JmOGNjMTFiMzQ1NmUxNzU5Y2QuanBn&sign=yx:tkn1kDIDxAntPyrD5VHxD_aM_pg=&tv=0_0)

![](https://fastly.jsdelivr.net/gh/bucketio/img14@main/2026/08/13/1786639015535-629aa3a5-8641-4561-a0f9-36f0d5546ebd.png)

## 解决方案

引入依赖（SPI 机制）：

```xml
<!-- WebP ImageIO 解码支持 -->
<dependency>
  <groupId>org.sejda.imageio</groupId>
  <artifactId>webp-imageio</artifactId>
  <version>0.1.6</version>
</dependency>
```

无需改动代码，就可以拿到图片元数据：

```json
{
  "data": {
    "numBands": 3,
    "colorSpaceNumComponents": 3,
    "alphaPremultiplied": false,
    "colorSpaceType": 5,
    "transparency": 1,
    "bitDepth": 24,
    "width": 1080,
    "format": "unknown",
    "colorModel": "java.awt.image.DirectColorModel",
    "imageType": 1,
    "url": "https://kkimgs.yisou.com/ims?kt=url&at=ori&key=aHR0cHM6Ly93d3cubG9uZy1waG90by5jb20vdXBsb2Fkcy8yMDIwLzA4MTEvNmFlMGM0OGIxZDllN2JmOGNjMTFiMzQ1NmUxNzU5Y2QuanBn&sign=yx:tkn1kDIDxAntPyrD5VHxD_aM_pg=&tv=0_0",
    "height": 732
  },
  "success": true
}
```

其他原来可以支持的格式也依旧正常支持。
