import { useRef, useState } from "react";
import type { ChangeEvent } from "react";

import { profileApi } from "../api/profileApi";
import { ApiError } from "../../core/api/httpClient";

interface AvatarUploadProps {
    avatarUrl: string | null;
    initials: string;
    onChange: (avatarUrl: string | null) => void;
}

export function AvatarUpload({ avatarUrl, initials, onChange }: AvatarUploadProps) {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [brokenImage, setBrokenImage] = useState(false);

    const showImage = Boolean(avatarUrl) && !brokenImage;

    function handleClick() {
        if (busy) return;
        fileInputRef.current?.click();
    }

    async function handleFileSelected(event: ChangeEvent<HTMLInputElement>) {
        const file = event.target.files?.[0];
        event.target.value = "";
        if (!file) return;

        setBusy(true);
        setError(null);
        try {
            const updated = await profileApi.uploadAvatar(file);
            setBrokenImage(false);
            onChange(updated.avatarUrl);
        } catch (err) {
            setError(
                err instanceof ApiError && err.status === 422
                    ? "Invalid file. Only JPEG, PNG, or WebP images up to 5 MB are accepted."
                    : "Failed to upload avatar.",
            );
        } finally {
            setBusy(false);
        }
    }

    async function handleRemove() {
        if (busy) return;
        setBusy(true);
        setError(null);
        try {
            await profileApi.deleteAvatar();
            onChange(null);
        } catch {
            setError("Failed to remove avatar.");
        } finally {
            setBusy(false);
        }
    }

    return (
        <div className="avatar-upload">
            <button
                type="button"
                className="avatar avatar-button"
                onClick={handleClick}
                disabled={busy}
                title="Click to upload a new avatar"
            >
                {showImage ? (
                    <img
                        src={avatarUrl ?? undefined}
                        alt="Profile avatar"
                        className="avatar-img"
                        onError={() => setBrokenImage(true)}
                    />
                ) : (
                    <span>{initials || "?"}</span>
                )}
                <span className="avatar-overlay">{busy ? <span className="avatar-spinner" /> : "Change"}</span>
            </button>
            <input
                ref={fileInputRef}
                type="file"
                accept="image/jpeg,image/png,image/webp"
                hidden
                onChange={(event) => void handleFileSelected(event)}
            />
            {showImage && (
                <button type="button" className="avatar-remove-btn" onClick={() => void handleRemove()} disabled={busy}>
                    Remove photo
                </button>
            )}
            {error && (
                <p className="alert avatar-error" role="alert">
                    {error}
                </p>
            )}
        </div>
    );
}
