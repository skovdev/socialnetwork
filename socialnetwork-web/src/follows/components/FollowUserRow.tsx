import { Link } from "react-router-dom";

import { FollowButton } from "./FollowButton";
import type { FollowUser } from "../types";
import { Avatar } from "../../shared/components/Avatar";

interface FollowUserRowProps {
    row: FollowUser;
    isOwnRow: boolean;
}

export function FollowUserRow({ row, isOwnRow }: FollowUserRowProps) {
    return (
        <div className="follow-user-row">
            <Link to={`/users/${row.user.username}`} className="identity-row">
                <Avatar avatarUrl={row.user.avatarUrl} displayName={row.user.displayName} />
                <div className="identity-text">
                    <span>{row.user.displayName}</span>
                    <span className="username">@{row.user.username}</span>
                </div>
            </Link>
            {!isOwnRow && <FollowButton username={row.user.username} initiallyFollowed={row.followedByCurrentUser} />}
        </div>
    );
}
