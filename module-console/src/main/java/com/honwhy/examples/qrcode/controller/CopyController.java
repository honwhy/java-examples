package com.honwhy.examples.qrcode.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class CopyController {

    @GetMapping("/image")
    public void callback(@RequestParam(required = false) String url,
                         @RequestParam(required = false) Integer level,
                         HttpServletResponse response) throws Exception {
        if (!StringUtils.hasText(url)) {
            url = "https://honwhy.wang/posts/2025-09-01-github-cf-pages-blog-en/";
        }

        // Set default error correction level if not provided
        int errorCorrectionLevel = (level != null) ? level : 1;

        // Generate QR code
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<>();
        map.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel(errorCorrectionLevel));
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,
                300, 300,
                map);

        // Print QR code to console
        printQRCodeToConsole(
                qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,1, 1,map),
                url
        );

        // Convert to image
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

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
    private com.google.zxing.qrcode.decoder.ErrorCorrectionLevel getErrorCorrectionLevel(int level) {
        switch (level) {
            case 0: return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L;
            case 1: return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M;
            case 2: return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.Q;
            case 3: return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H;
            default: return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M;
        }
    }
}