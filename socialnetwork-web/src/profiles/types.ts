export type FamilyStatus =
    | "SINGLE"
    | "MARRIED"
    | "DIVORCED"
    | "WIDOWED"
    | "SEPARATED"
    | "ENGAGED"
    | "IN_A_RELATIONSHIP"
    | "COHABITATING";

export interface UserProfile {
    username: string;
    displayName: string;
    firstName: string;
    lastName: string;
    bio: string | null;
    avatarUrl: string | null;
    birthDate: string | null;
    country: string | null;
    city: string | null;
}

export interface UpdateProfileRequest {
    displayName: string;
    bio?: string;
    birthDate?: string;
    phoneNumber?: string;
    country?: string;
    city?: string;
    address?: string;
    familyStatus?: FamilyStatus;
}
