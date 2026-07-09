package local.socialnetwork.profiles.service.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvatarImageSignatureValidatorTest {

    private static final byte[] JPEG_BYTES = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x01, 0x02};
    private static final byte[] PNG_BYTES =
            {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00};
    private static final byte[] WEBP_BYTES =
            {0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x57, 0x45, 0x42, 0x50};

    @Test
    void matches_whenJpegBytesAndJpegType_returnsTrue() {
        assertThat(AvatarImageSignatureValidator.matches("image/jpeg", JPEG_BYTES)).isTrue();
    }

    @Test
    void matches_whenPngBytesAndPngType_returnsTrue() {
        assertThat(AvatarImageSignatureValidator.matches("image/png", PNG_BYTES)).isTrue();
    }

    @Test
    void matches_whenWebpBytesAndWebpType_returnsTrue() {
        assertThat(AvatarImageSignatureValidator.matches("image/webp", WEBP_BYTES)).isTrue();
    }

    @Test
    void matches_whenContentDoesNotMatchDeclaredType_returnsFalse() {
        assertThat(AvatarImageSignatureValidator.matches("image/png", JPEG_BYTES)).isFalse();
    }

    @Test
    void matches_whenContentIsHtmlDisguisedAsPng_returnsFalse() {
        var htmlBytes = "<script>alert(1)</script>".getBytes();

        assertThat(AvatarImageSignatureValidator.matches("image/png", htmlBytes)).isFalse();
    }

    @Test
    void matches_whenContentShorterThanSignature_returnsFalse() {
        assertThat(AvatarImageSignatureValidator.matches("image/png", new byte[]{(byte) 0x89, 0x50})).isFalse();
    }

    @Test
    void matches_whenWebpMissingFormatMarker_returnsFalse() {
        var riffOnly = new byte[]{0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        assertThat(AvatarImageSignatureValidator.matches("image/webp", riffOnly)).isFalse();
    }

    @Test
    void matches_whenUnsupportedDeclaredType_returnsFalse() {
        assertThat(AvatarImageSignatureValidator.matches("image/gif", PNG_BYTES)).isFalse();
    }
}
