import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { profileApi } from "../api/profileApi";
import { ApiError } from "../../core/api/httpClient";
import type { PublicUserProfile } from "../types";

export function UserProfilePage() {
    const { username } = useParams<{ username: string }>();

    const [profile, setProfile] = useState<PublicUserProfile | null>(null);
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

    const initials = profile
        ? `${profile.firstName?.[0] ?? ""}${profile.lastName?.[0] ?? ""}`.toUpperCase()
        : "";

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <div>
                    <Link to="/posts" className="btn btn-secondary">
                        Posts
                    </Link>
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
                    <div className="profile-card">
                        <div className="avatar">
                            {profile.avatarUrl ? (
                                <img src={profile.avatarUrl} alt="Profile avatar" className="avatar-img" />
                            ) : (
                                <span>{initials || "?"}</span>
                            )}
                        </div>
                        <h1>{profile.displayName}</h1>
                        <p className="username">@{profile.username}</p>
                        {profile.bio && <p className="bio">{profile.bio}</p>}
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
                )}
            </main>
        </>
    );
}
