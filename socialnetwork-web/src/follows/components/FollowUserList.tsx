import { useCallback, useEffect, useState } from "react";

import { followApi } from "../api/followApi";
import { FollowUserRow } from "./FollowUserRow";
import type { FollowUser } from "../types";
import { ApiError } from "../../core/api/httpClient";
import type { CurrentUser } from "../../shared/types";

interface FollowUserListProps {
    username: string;
    mode: "followers" | "following";
    currentUser: CurrentUser | null;
    emptyMessage?: string;
}

export function FollowUserList({ username, mode, currentUser, emptyMessage = "Nobody here yet." }: FollowUserListProps) {
    const [users, setUsers] = useState<FollowUser[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchPage = useCallback(
        (pageNumber: number) =>
            mode === "followers" ? followApi.getFollowers(username, pageNumber) : followApi.getFollowing(username, pageNumber),
        [username, mode],
    );

    useEffect(() => {
        setUsers([]);
        setPage(0);
        setIsLoading(true);
        setError(null);
        fetchPage(0)
            .then((loaded) => {
                setUsers(loaded.content);
                setTotalPages(loaded.page.totalPages);
            })
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load users"))
            .finally(() => setIsLoading(false));
    }, [fetchPage]);

    async function handleLoadMore() {
        setIsLoadingMore(true);
        try {
            const next = page + 1;
            const loaded = await fetchPage(next);
            setUsers((prev) => [...prev, ...loaded.content]);
            setPage(next);
            setTotalPages(loaded.page.totalPages);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to load users");
        } finally {
            setIsLoadingMore(false);
        }
    }

    return (
        <>
            {error && (
                <p className="alert" role="alert">
                    {error}
                </p>
            )}
            {isLoading && <p className="hint">Loading…</p>}
            {!isLoading && users.length === 0 && <p className="hint">{emptyMessage}</p>}

            <div className="follow-user-list">
                {users.map((row) => (
                    <FollowUserRow key={row.id} row={row} isOwnRow={currentUser?.username === row.user.username} />
                ))}
            </div>

            {page + 1 < totalPages && (
                <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => void handleLoadMore()}
                    disabled={isLoadingMore}
                >
                    {isLoadingMore ? "Loading…" : "Load more"}
                </button>
            )}
        </>
    );
}
