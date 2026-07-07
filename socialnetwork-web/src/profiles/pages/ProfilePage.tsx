import { useEffect, useState } from "react";

import { profileApi } from "../api/profileApi";
import { ApiError } from "../../core/api/httpClient";
import type { UserProfile } from "../types";
import { useAuth } from "../../auth/hooks/AuthContext";

export function ProfilePage() {
    const { logout } = useAuth();

    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        profileApi
            .getMyProfile()
            .then(setProfile)
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load profile"));
    }, []);

    if (error) {
        return (
            <div className="page-center">
                <p className="alert" role="alert">
                    {error}
                </p>
            </div>
        );
    }

    if (!profile) {
        return (
            <div className="page-center">
                <p className="hint">Loading…</p>
            </div>
        );
    }

    const initials = `${profile.firstName?.[0] ?? ""}${profile.lastName?.[0] ?? ""}`.toUpperCase();

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <button type="button" className="btn btn-secondary" onClick={() => void logout()}>
                    Log out
                </button>
            </header>
            <main className="profile-main">
                <div className="profile-card">
                    <div className="avatar">{initials || "?"}</div>
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
            </main>
        </>
    );
}
