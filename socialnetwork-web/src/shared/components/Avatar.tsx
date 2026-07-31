import { useState } from "react";

interface AvatarProps {
    avatarUrl?: string | null;
    displayName: string;
    size?: "sm" | "lg";
}

function initialsOf(displayName: string): string {
    return displayName
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((word) => word[0])
        .join("")
        .toUpperCase();
}

export function Avatar({ avatarUrl, displayName, size = "sm" }: AvatarProps) {
    const [broken, setBroken] = useState(false);
    const showImage = Boolean(avatarUrl) && !broken;

    return (
        <span className={`avatar-circle avatar-circle-${size}`}>
            {showImage ? (
                <img src={avatarUrl ?? undefined} alt="" className="avatar-circle-img" onError={() => setBroken(true)} />
            ) : (
                <span>{initialsOf(displayName) || "?"}</span>
            )}
        </span>
    );
}
