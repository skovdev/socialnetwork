import { useEffect, useState } from "react";

import { followApi } from "../api/followApi";
import type { FollowSummary } from "../types";
import { ApiError } from "../../core/api/httpClient";

interface FollowButtonProps {
    username: string;
    initiallyFollowed: boolean;
    onChange?: (summary: FollowSummary) => void;
}

export function FollowButton({ username, initiallyFollowed, onChange }: FollowButtonProps) {
    const [followed, setFollowed] = useState(initiallyFollowed);
    const [isPending, setIsPending] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setFollowed(initiallyFollowed);
    }, [initiallyFollowed, username]);

    async function handleToggle() {
        if (isPending) return;
        setIsPending(true);
        setError(null);
        try {
            const summary = followed ? await followApi.unfollow(username) : await followApi.follow(username);
            setFollowed(summary.followedByCurrentUser);
            onChange?.(summary);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to update follow status");
        } finally {
            setIsPending(false);
        }
    }

    return (
        <span className="follow-button-wrap">
            <button
                type="button"
                className={followed ? "btn btn-secondary" : "btn"}
                onClick={() => void handleToggle()}
                disabled={isPending}
            >
                {isPending ? "…" : followed ? "Following" : "Follow"}
            </button>
            {error && (
                <p className="alert" role="alert">
                    {error}
                </p>
            )}
        </span>
    );
}
