package local.socialnetwork.userservice.util;

import local.socialnetwork.userservice.type.MimeType;

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

@Component
public class ResourceUtil {

    private static final String EMPTY = "";

    public String getEncodedResource(String path) {
        Resource resource = new ClassPathResource(path);
        try {
            return Base64.getEncoder().encodeToString(resource.getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeResource(MultipartFile file, String path) {
        try {
            byte[] byteFile = file.getBytes();
            var filePath = Paths.get(path + generateRandomFilename() + "." + getFormatFileFromMimeType(file.getContentType()));
            Files.write(filePath, byteFile);
            return Base64.getEncoder().encodeToString(byteFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public Object convertFromString(String value) {
        try {
            byte[] data = Base64.getDecoder().decode(value);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            return new RuntimeException(e);
        }
    }

    public String convertToString(Serializable object) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}