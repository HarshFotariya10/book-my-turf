package com.bookmyturf.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

public class FileStorageUtil {

    public static String saveBase64ToFile(String base64Data, String uploadDir) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) return null;

        String[] parts = base64Data.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];
        byte[] decodedBytes = Base64.getDecoder().decode(imageData);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = "img_" + UUID.randomUUID() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, decodedBytes);

        return filePath.toString();
    }

    public static String convertFileToBase64(String path) throws IOException {
        if (path == null || path.isEmpty()) return null;

        byte[] fileContent = Files.readAllBytes(Paths.get(path));
        String base64 = Base64.getEncoder().encodeToString(fileContent);
        return "data:image/jpeg;base64," + base64;
    }
}
