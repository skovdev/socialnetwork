export type FamilyStatus =
    | "SINGLE"
    | "MARRIED"
    | "DIVORCED"
    | "WIDOWED"
    | "SEPARATED"
    | "ENGAGED"
    | "IN_A_RELATIONSHIP"
    | "COHABITATING";

export interface PublicUserProfile {
    username: string;
    displayName: string;
    firstName: string;
    lastName: string;
    bio: string | null;
    avatarUrl: string | null;
    country: string | null;
    city: string | null;
    followerCount: number;
    followingCount: number;
    followedByCurrentUser: boolean;
}

export interface MyProfile extends PublicUserProfile {
    birthDate: string | null;
    phoneNumber: string | null;
    address: string | null;
    familyStatus: FamilyStatus | null;
}
// Note: MyProfile inherits followedByCurrentUser from PublicUserProfile, but the backend's
// /api/v1/profiles response never sends it (it is meaningless for your own profile) — it comes
// back undefined at runtime, and pages for your own profile simply never read it.

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
