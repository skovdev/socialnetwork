package local.socialnetwork.shared.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Helpers for client-supplied {@link Pageable}s.
 */
public final class Pages {

    private Pages() {
    }

    /**
     * Keeps only the page number and size. A client-supplied {@code sort} parameter is dropped so callers cannot
     * order by arbitrary (possibly sensitive) property paths such as {@code author.passwordHash}; every listing
     * has a fixed order defined by its repository method.
     */
    public static Pageable withoutClientSort(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }
}
