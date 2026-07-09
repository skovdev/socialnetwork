package local.socialnetwork.profiles.service.impl;

/**
 * Verifies that raw file content actually matches the magic-byte signature expected for a
 * declared image MIME type, defeating a spoofed multipart {@code Content-Type} header.
 */
final class AvatarImageSignatureValidator {

    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE =
            {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] RIFF_SIGNATURE = {0x52, 0x49, 0x46, 0x46};
    private static final byte[] WEBP_SIGNATURE = {0x57, 0x45, 0x42, 0x50};
    private static final int WEBP_FORMAT_OFFSET = 8;

    private AvatarImageSignatureValidator() {
    }

    /**
     * @param declaredContentType the MIME type claimed by the client (e.g. {@code image/png})
     * @param content             the raw file bytes actually uploaded
     * @return {@code true} if {@code content} starts with the magic-byte signature expected
     * for {@code declaredContentType}
     */
    static boolean matches(String declaredContentType, byte[] content) {
        return switch (declaredContentType) {
            case "image/jpeg" -> startsWith(content, JPEG_SIGNATURE, 0);
            case "image/png" -> startsWith(content, PNG_SIGNATURE, 0);
            case "image/webp" -> startsWith(content, RIFF_SIGNATURE, 0)
                    && startsWith(content, WEBP_SIGNATURE, WEBP_FORMAT_OFFSET);
            default -> false;
        };
    }

    private static boolean startsWith(byte[] content, byte[] signature, int offset) {
        if (content.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (content[offset + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
