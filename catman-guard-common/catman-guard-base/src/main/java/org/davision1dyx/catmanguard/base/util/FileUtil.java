package org.davision1dyx.catmanguard.base.util;

import org.davision1dyx.catmanguard.base.constant.CommonConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
public class FileUtil {

    public static String getFileType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return CommonConstant.UNKNOWN;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return CommonConstant.UNKNOWN;
    }

    public static void writeBytes(byte[] bytes, String filePath) throws IOException {
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            fos.write(bytes);
        }
    }

    public static void unzip(String zipFilePath, String extractDir) throws IOException {
        Path extractPath = Paths.get(extractDir);
        Files.createDirectories(extractPath);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = extractPath.resolve(entry.getName());

                // 安全检查：防止 ZIP 路径遍历攻击
                if (!entryPath.normalize().startsWith(extractPath.normalize())) {
                    continue;
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    public static String getImageContentType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".gif")) return "image/gif";
        if (lowerName.endsWith(".webp")) return "image/webp";
        if (lowerName.endsWith(".bmp")) return "image/bmp";
        return "application/octet-stream";
    }

    public static String readFileToString(Path mdFile) throws IOException {
        return Files.readString(mdFile);
    }

    public static byte[] readFileToBytes(Path imagePath) throws IOException {
        return Files.readAllBytes(imagePath);
    }
}
