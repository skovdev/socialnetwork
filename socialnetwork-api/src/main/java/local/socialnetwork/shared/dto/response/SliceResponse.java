package local.socialnetwork.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * A page of results that knows whether another page follows but not the total count, so serving it never
 * needs a {@code COUNT(*)} over the whole table. Mirrors the {@code content}/{@code page} layout of
 * Spring's {@code PagedModel}.
 */
@Schema(description = "A slice of results with a flag telling whether more exist")
public record SliceResponse<T>(List<T> content, SliceInfo page) {

    public record SliceInfo(int size, int number, boolean hasNext) {
    }

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(slice.getContent(),
                new SliceInfo(slice.getSize(), slice.getNumber(), slice.hasNext()));
    }
}
