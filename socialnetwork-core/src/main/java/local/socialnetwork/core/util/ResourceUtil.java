package local.socialnetwork.core.util;

import local.socialnetwork.core.type.MimeType;

import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.UUID;

@Component
public class ResourceUtil {

    private static final String EMPTY = "";

    public String getEncodedResource(String path) throws IOException {

        var resourcePath = Paths.get(path);

        byte[] byteResourcePath = Files.readAllBytes(resourcePath);

        return Base64.getEncoder().encodeToString(byteResourcePath);

    }

    public String writeResource(MultipartFile file, String path) throws IOException {

        byte[] byteFile = file.getBytes();

        var filePath = Paths.get(path + generateRandomFilename() + "." + getFormatFileFromMimeType(file.getContentType()));

        Files.write(filePath, byteFile);

        return Base64.getEncoder().encodeToString(byteFile);

    }

    public String getFormatFileFromMimeType(String mimeType) {

        for (MimeType type : MimeType.values()) {

            if (type.getType().equalsIgnoreCase(mimeType)) {
                return type.getFormat();
            }
        }

        return EMPTY;

    }

    public String generateRandomFilename() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}