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
}

export interface MyProfile extends PublicUserProfile {
    birthDate: string | null;
    phoneNumber: string | null;
    address: string | null;
    familyStatus: FamilyStatus | null;
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
