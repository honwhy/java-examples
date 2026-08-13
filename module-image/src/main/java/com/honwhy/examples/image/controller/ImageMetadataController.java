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
