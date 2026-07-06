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
        return <p role="alert">{error}</p>;
    }

    if (!profile) {
        return <p>Loading…</p>;
    }

    return (
        <div>
            <h1>{profile.displayName}</h1>
            <p>@{profile.username}</p>
            {profile.bio && <p>{profile.bio}</p>}
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
            <button type="button" onClick={() => void logout()}>
                Log out
            </button>
        </div>
    );
}
