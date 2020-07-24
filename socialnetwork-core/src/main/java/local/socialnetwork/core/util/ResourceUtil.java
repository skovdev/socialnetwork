package local.socialnetwork.core.util;

import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Base64;

@Component
public class ResourceUtil {

    public String getEncodedResource(String path) throws IOException {

        var resourcePath = Paths.get(path);

        byte[] byteResourcePath = Files.readAllBytes(resourcePath);

        return Base64.getEncoder().encodeToString(byteResourcePath);

    }

    public String writeResource(MultipartFile file, String path) throws IOException {

        byte[] byteFile = file.getBytes();

        var filePath = Paths.get(path + file.getOriginalFilename());

        Files.write(filePath, byteFile);

        return Base64.getEncoder().encodeToString(byteFile);

    }
}