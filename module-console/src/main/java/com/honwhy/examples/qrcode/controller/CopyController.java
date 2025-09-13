package com.honwhy.examples.qrcode.controller;

import com.github.aytchell.qrgen.QrGenerator;
import com.github.aytchell.qrgen.colors.RgbValue;
import com.github.aytchell.qrgen.config.ImageFileType;
import com.github.aytchell.qrgen.config.MarkerStyle;
import com.github.aytchell.qrgen.config.PixelStyle;
import com.github.aytchell.qrgen.exceptions.QrConfigurationException;
import com.github.aytchell.qrgen.renderers.QrCodeRenderer;
import com.github.aytchell.qrgen.utils.ColorConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class CopyController {

    @GetMapping("/image")
    public void image(@RequestParam(required = false) String url,
                         @RequestParam(required = false) Integer level,
                         @RequestParam(required = false) Boolean rounded, // 新增圆点参数
                         HttpServletResponse response) throws Exception {
        if (!StringUtils.hasText(url)) {
            url = "https://honwhy.wang/posts/2025-09-01-github-cf-pages-blog-en/";
        }

        // Set default error correction level if not provided
        int errorCorrectionLevel = (level != null) ? level : 1;
        boolean useRoundedDots = Boolean.TRUE.equals(rounded); // 默认false


        // Print QR code to console
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<>();
        map.put(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel(errorCorrectionLevel));
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        printQRCodeToConsole(
                qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,1, 1,map),
                url
        );
        // Convert to image
        BufferedImage bufferedImage = useRoundedDots ?
                createRoundedQRCodeImage(url, 300, errorCorrectionLevel) :
                createNormalQRCodeImage(url, 300, errorCorrectionLevel);
        BufferedImage consoleImage = createNormalQRCodeImage(url, 1, errorCorrectionLevel);
        if (!useRoundedDots) {
            printImageToConsole(consoleImage,"square");
        } else {
            printImageToConsole(consoleImage, "dot");
        }
        // Set response headers for image
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "max-age=3600"); // Cache for 1 hour

        // Write image directly to response output stream
        ImageIO.write(bufferedImage, "PNG", response.getOutputStream());
        response.flushBuffer();
    }
    private void printQRCodeToConsole(BitMatrix bitMatrix, String url)  throws Exception {
        System.out.println("\n=== QR Code for: " + url + " ===");
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        // Simple ASCII representation
        for (int y = 0; y < height; y++) { // Skip every other row for better aspect ratio
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    row.append("██"); // Two characters for better square appearance
                } else {
                    row.append("  "); // Two spaces
                }
            }
            System.out.println(row);
        }
        System.out.println("=== End QR Code ===\n");
    }
    private BufferedImage createNormalQRCodeImage(String url, int width, int level) throws QrConfigurationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, WriterException {
        // Generate QR code
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<>();
        map.put(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel(level));
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,
                width, width,
                map);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    // 新增方法：创建圆点二维码图像
    private BufferedImage createRoundedQRCodeImage(String url, int width, int level) throws QrConfigurationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, WriterException {
        QrCodeRenderer qrCodeRenderer = new QrCodeRenderer(PixelStyle.ROWS, MarkerStyle.ROUND_CORNERS);
        ColorConfig colorConfig = new ColorConfig(new RgbValue(0, 0, 0), new RgbValue(255, 255, 255));
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel(level));
        return qrCodeRenderer.encodeAndRender(url, colorConfig, width, width, hints);
    }

    private void printImageToConsole(BufferedImage image, String pixelStyle) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y += 1) {
            for (int x = 0; x < width; x += 1) {
                int rgb = image.getRGB(x, y);
                // Simple threshold: black if darker than 50% gray
                if ((rgb & 0xFF) < 128) { // Check blue channel for simplicity
                    if ("dot".equalsIgnoreCase(pixelStyle)) {
                        System.out.print("● "); // dot style
                    } else {
                        System.out.print("██"); // square style (default)
                    }
                } else {
                    System.out.print("  "); // empty space
                }
            }
            System.out.println();
        }
    }
    private void printQRCodeToConsole(BitMatrix bitMatrix, String url, boolean rounded) throws Exception {
        System.out.println("\n=== QR Code for: " + url + " ===");
        System.out.println("Style: " + (rounded ? "Rounded dots" : "Square dots"));

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        // 根据圆点参数选择不同的字符
        String filledChar = rounded ? "● " : "██";
        String emptyChar = "  ";

        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    row.append(filledChar);
                } else {
                    row.append(emptyChar);
                }
            }
            System.out.println(row);
        }
        System.out.println("=== End QR Code ===\n");
    }
    private ErrorCorrectionLevel getErrorCorrectionLevel(int level) {
        switch (level) {
            case 0: return ErrorCorrectionLevel.L;
            case 1: return ErrorCorrectionLevel.M;
            case 2: return ErrorCorrectionLevel.Q;
            case 3: return ErrorCorrectionLevel.H;
            default: return ErrorCorrectionLevel.M;
        }
    }
}