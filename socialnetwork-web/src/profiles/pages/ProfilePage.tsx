import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { profileApi } from "../api/profileApi";
import { AvatarUpload } from "../components/AvatarUpload";
import { formatFamilyStatus } from "../familyStatus";
import { ApiError } from "../../core/api/httpClient";
import type { MyProfile } from "../types";
import { useAuth } from "../../auth/hooks/AuthContext";

export function ProfilePage() {
    const { logout } = useAuth();

    const [profile, setProfile] = useState<MyProfile | null>(null);
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
    const familyStatusLabel = formatFamilyStatus(profile.familyStatus);

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
                    <button type="button" className="btn btn-secondary" onClick={() => void logout()}>
                        Log out
                    </button>
                </div>
            </header>
            <main className="profile-main">
                <div className="profile-card">
                    <AvatarUpload
                        avatarUrl={profile.avatarUrl}
                        initials={initials}
                        onChange={(avatarUrl) => setProfile({ ...profile, avatarUrl })}
                    />
                    <h1>{profile.displayName}</h1>
                    <p className="username">@{profile.username}</p>
                    {profile.bio && <p className="bio">{profile.bio}</p>}
                    <Link to="/profile/edit" className="btn btn-secondary">
                        Edit profile
                    </Link>
                    <dl>
                        <dt>Name</dt>
                        <dd>
                            {profile.firstName} {profile.lastName}
                        </dd>
                        {profile.birthDate && <dt>Birth date</dt>}
                        {profile.birthDate && <dd>{profile.birthDate}</dd>}
                        {familyStatusLabel && <dt>Relationship status</dt>}
                        {familyStatusLabel && <dd>{familyStatusLabel}</dd>}
                        {profile.phoneNumber && <dt>Phone</dt>}
                        {profile.phoneNumber && <dd>{profile.phoneNumber}</dd>}
                        {profile.country && <dt>Country</dt>}
                        {profile.country && <dd>{profile.country}</dd>}
                        {profile.city && <dt>City</dt>}
                        {profile.city && <dd>{profile.city}</dd>}
                        {profile.address && <dt>Address</dt>}
                        {profile.address && <dd>{profile.address}</dd>}
                    </dl>
                </div>
            </main>
        </>
    );
}
