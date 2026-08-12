import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { profileApi } from "../../profiles/api/profileApi";
import { useAuth } from "../../auth/hooks/AuthContext";
import { PostList } from "../components/PostList";
import type { CurrentUser } from "../../shared/types";

export function FeedPage() {
    const { logout } = useAuth();
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);

    useEffect(() => {
        profileApi
            .getMyProfile()
            .then((profile) =>
                setCurrentUser({ username: profile.username, displayName: profile.displayName, avatarUrl: profile.avatarUrl }),
            )
            .catch(() => {});
    }, []);

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <div>
                    <Link to="/profile" className="btn btn-secondary">
                        My profile
                    </Link>
                    <button type="button" className="btn btn-secondary" onClick={() => void logout()}>
                        Log out
                    </button>
                </div>
            </header>
            <main className="profile-main">
                <PostList
                    currentUser={currentUser}
                    showComposer
                    emptyMessage="No posts yet. Be the first to share something!"
                />
            </main>
        </>
    );
}
