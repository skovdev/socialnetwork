package local.socialnetwork.profileservice.util;

import local.socialnetwork.profileservice.type.MimeType;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.UUID;
import java.util.Base64;

@Slf4j
@Component
public class ResourceUtil {

    private static final String EMPTY = "";

    public String getEncodedResource(String path) {
        Resource resource = new ClassPathResource(path);
        try {
            return Base64.getEncoder().encodeToString(resource.getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("Failed to encode resource");
            throw new RuntimeException(e);
        }
    }

    public String writeResource(MultipartFile file, String path) {
        try {
            byte[] byteFile = file.getBytes();
            var filePath = Paths.get(new ClassPathResource(path).getPath() + generateRandomFilename() + "." + getFormatFileFromMimeType(file.getContentType()));
            // Ensure the directory exists
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, byteFile);
            return Base64.getEncoder().encodeToString(byteFile);
        } catch (IOException e) {
            log.error("Failed to write resource");
            throw new RuntimeException(e);
        }
    }

    public String generateRandomFilename() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getFormatFileFromMimeType(String mimeType) {
        for (MimeType type : MimeType.values()) {
            if (type.getType().equalsIgnoreCase(mimeType)) {
                return type.getFormat();
            }
        }
        return EMPTY;
    }

    public Object convertFromString(String value) {
        try {
            byte[] data = Base64.getDecoder().decode(value);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object readObject = ois.readObject();
            ois.close();
            return readObject;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Failed to convert from String");
            throw new RuntimeException(e);
        }
    }

    public String convertToString(Serializable object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            log.error("Failed to convert to String");
            throw new RuntimeException(e);
        }
    }
}