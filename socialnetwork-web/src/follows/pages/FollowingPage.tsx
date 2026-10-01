import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { FollowUserList } from "../components/FollowUserList";
import { profileApi } from "../../profiles/api/profileApi";
import type { CurrentUser } from "../../shared/types";

export function FollowingPage() {
    const { username } = useParams<{ username: string }>();
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);

    useEffect(() => {
        profileApi
            .getMyProfile()
            .then((profile) =>
                setCurrentUser({ username: profile.username, displayName: profile.displayName, avatarUrl: profile.avatarUrl }),
            )
            .catch(() => {});
    }, []);

    if (!username) {
        return null;
    }

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <div>
                    <Link to={`/users/${username}`} className="btn btn-secondary">
                        Back to profile
                    </Link>
                    <Link to="/feed" className="btn btn-secondary">
                        Feed
                    </Link>
                </div>
            </header>
            <main className="profile-main">
                <h1>People @{username} follows</h1>
                <FollowUserList
                    username={username}
                    mode="following"
                    currentUser={currentUser}
                    emptyMessage="Not following anyone yet."
                />
            </main>
        </>
    );
}
