import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { profileApi } from "../api/profileApi";
import { ApiError } from "../../core/api/httpClient";
import type { PublicUserProfile } from "../types";
import { PostList } from "../../posts/components/PostList";
import { Avatar } from "../../shared/components/Avatar";
import type { CurrentUser } from "../../shared/types";

export function UserProfilePage() {
    const { username } = useParams<{ username: string }>();

    const [profile, setProfile] = useState<PublicUserProfile | null>(null);
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!username) return;
        setProfile(null);
        setError(null);
        profileApi
            .getProfileByUsername(username)
            .then(setProfile)
            .catch((err) =>
                setError(err instanceof ApiError && err.status === 404 ? "User not found." : "Failed to load profile"),
            );
    }, [username]);

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
                </div>
            </header>
            <main className="profile-main">
                {error && (
                    <p className="alert" role="alert">
                        {error}
                    </p>
                )}
                {!error && !profile && <p className="hint">Loading…</p>}
                {!error && profile && (
                    <>
                        <div className="profile-card">
                            <div className="identity-row">
                                <Avatar avatarUrl={profile.avatarUrl} displayName={profile.displayName} size="lg" />
                                <div className="identity-text">
                                    <h1>{profile.displayName}</h1>
                                    <p className="username">@{profile.username}</p>
                                    {profile.bio && <p className="bio">{profile.bio}</p>}
                                </div>
                            </div>

                            <dl>
                                <dt>Name</dt>
                                <dd>
                                    {profile.firstName} {profile.lastName}
                                </dd>
                                {profile.city && <dt>City</dt>}
                                {profile.city && <dd>{profile.city}</dd>}
                                {profile.country && <dt>Country</dt>}
                                {profile.country && <dd>{profile.country}</dd>}
                            </dl>
                        </div>

                        <PostList username={profile.username} currentUser={currentUser} showComposer={false} />
                    </>
                )}
            </main>
        </>
    );
}
