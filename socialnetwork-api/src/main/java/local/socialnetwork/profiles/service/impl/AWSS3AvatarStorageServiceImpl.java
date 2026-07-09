package local.socialnetwork.profiles.service.impl;

import local.socialnetwork.core.config.AWSS3Properties;

import local.socialnetwork.profiles.service.AvatarStorageService;

import local.socialnetwork.shared.exception.InvalidAvatarFileException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3AvatarStorageServiceImpl implements AvatarStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AWSS3Properties awsS3Properties;

    @Override
    public String upload(UUID authUserId, MultipartFile file) {
        var content = readContent(file);
        validate(file, content);
        var key = buildKey(authUserId, file.getContentType());
        try {
            var request = PutObjectRequest.builder()
                    .bucket(awsS3Properties.avatarBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(content));
            log.info("Avatar uploaded for user {}: key={}", authUserId, key);
            return key;
        } catch (S3Exception e) {
            log.error("S3 upload failed for user {}: {}", authUserId, e.awsErrorDetails());
            throw new InvalidAvatarFileException("Avatar upload failed due to a storage error");
        }
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(awsS3Properties.avatarBucketName())
                    .key(key)
                    .build());
            log.info("Avatar deleted from S3: {}", key);
        } catch (S3Exception e) {
            log.error("S3 delete failed for key {}: {}", key, e.awsErrorDetails());
            throw new InvalidAvatarFileException("Avatar deletion failed due to a storage error");
        }
    }

    @Override
    public String presign(String key) {
        if (key == null) {
            return null;
        }
        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(awsS3Properties.avatarPresignedUrlDuration())
                .getObjectRequest(get -> get.bucket(awsS3Properties.avatarBucketName()).key(key))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private byte[] readContent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidAvatarFileException("Avatar file must not be null or empty");
        }
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error("Failed to read avatar file: {}", e.getMessage());
            throw new InvalidAvatarFileException("Avatar upload failed: could not read the provided file");
        }
    }

    private void validate(MultipartFile file, byte[] content) {
        var contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidAvatarFileException(
                    "Unsupported file type '" + contentType + "'. Allowed: " + ALLOWED_CONTENT_TYPES);
        }
        if (content.length > MAX_FILE_SIZE_BYTES) {
            throw new InvalidAvatarFileException("File size " + content.length + " bytes exceeds the 5 MB limit");
        }
        if (!AvatarImageSignatureValidator.matches(contentType, content)) {
            throw new InvalidAvatarFileException(
                    "File content does not match the declared type '" + contentType + "'");
        }
    }

    private String buildKey(UUID authUserId, String contentType) {
        return "avatars/%s/%s.%s".formatted(authUserId, UUID.randomUUID(), extensionFor(contentType));
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new InvalidAvatarFileException("Unsupported content type: " + contentType);
        };
    }
}