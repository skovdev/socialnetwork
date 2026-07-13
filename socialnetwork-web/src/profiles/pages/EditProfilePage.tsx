import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";

import { profileApi } from "../api/profileApi";
import { FAMILY_STATUS_OPTIONS } from "../familyStatus";
import { ApiError } from "../../core/api/httpClient";
import type { FamilyStatus, UpdateProfileRequest } from "../types";

interface EditProfileForm {
    displayName: string;
    bio: string;
    birthDate: string;
    phoneNumber: string;
    country: string;
    city: string;
    address: string;
    familyStatus: FamilyStatus | "";
}

const emptyForm: EditProfileForm = {
    displayName: "",
    bio: "",
    birthDate: "",
    phoneNumber: "",
    country: "",
    city: "",
    address: "",
    familyStatus: "",
};

export function EditProfilePage() {
    const navigate = useNavigate();

    const [form, setForm] = useState<EditProfileForm>(emptyForm);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        profileApi
            .getMyProfile()
            .then((profile) =>
                setForm({
                    displayName: profile.displayName,
                    bio: profile.bio ?? "",
                    birthDate: profile.birthDate ?? "",
                    phoneNumber: profile.phoneNumber ?? "",
                    country: profile.country ?? "",
                    city: profile.city ?? "",
                    address: profile.address ?? "",
                    familyStatus: profile.familyStatus ?? "",
                }),
            )
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load profile"))
            .finally(() => setIsLoading(false));
    }, []);

    function updateField<K extends keyof EditProfileForm>(field: K, value: EditProfileForm[K]) {
        setForm((prev) => ({ ...prev, [field]: value }));
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setError(null);
        setIsSubmitting(true);
        try {
            const request: UpdateProfileRequest = {
                displayName: form.displayName,
                bio: form.bio || undefined,
                birthDate: form.birthDate || undefined,
                phoneNumber: form.phoneNumber || undefined,
                country: form.country || undefined,
                city: form.city || undefined,
                address: form.address || undefined,
                familyStatus: form.familyStatus || undefined,
            };
            await profileApi.updateMyProfile(request);
            navigate("/profile");
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to update profile");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <Link to="/profile" className="btn btn-secondary">
                    Cancel
                </Link>
            </header>
            <main className="profile-main">
                {isLoading ? (
                    <p className="hint">Loading…</p>
                ) : (
                    <div className="profile-card">
                        <h1>Edit profile</h1>
                        <form onSubmit={handleSubmit}>
                            {error && (
                                <p className="alert" role="alert">
                                    {error}
                                </p>
                            )}
                            <label className="field">
                                <span>Display name</span>
                                <input
                                    value={form.displayName}
                                    onChange={(e) => updateField("displayName", e.target.value)}
                                    required
                                />
                            </label>
                            <label className="field">
                                <span>Bio</span>
                                <textarea
                                    value={form.bio}
                                    onChange={(e) => updateField("bio", e.target.value)}
                                    rows={3}
                                    maxLength={500}
                                />
                            </label>
                            <label className="field">
                                <span>Date of birth</span>
                                <input
                                    type="date"
                                    value={form.birthDate}
                                    onChange={(e) => updateField("birthDate", e.target.value)}
                                />
                            </label>
                            <label className="field">
                                <span>Relationship status</span>
                                <select
                                    value={form.familyStatus}
                                    onChange={(e) => updateField("familyStatus", e.target.value as FamilyStatus | "")}
                                >
                                    <option value="">Prefer not to say</option>
                                    {FAMILY_STATUS_OPTIONS.map((option) => (
                                        <option key={option.value} value={option.value}>
                                            {option.label}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            <label className="field">
                                <span>Phone number</span>
                                <input
                                    value={form.phoneNumber}
                                    onChange={(e) => updateField("phoneNumber", e.target.value)}
                                />
                            </label>
                            <label className="field">
                                <span>Country</span>
                                <input
                                    value={form.country}
                                    onChange={(e) => updateField("country", e.target.value)}
                                />
                            </label>
                            <label className="field">
                                <span>City</span>
                                <input
                                    value={form.city}
                                    onChange={(e) => updateField("city", e.target.value)}
                                />
                            </label>
                            <label className="field">
                                <span>Address</span>
                                <input
                                    value={form.address}
                                    onChange={(e) => updateField("address", e.target.value)}
                                />
                            </label>
                            <button type="submit" className="btn" disabled={isSubmitting}>
                                {isSubmitting ? "Saving…" : "Save changes"}
                            </button>
                        </form>
                    </div>
                )}
            </main>
        </>
    );
}
