import { apiRequest } from "../../core/api/httpClient";
import type { MyProfile, PublicUserProfile, UpdateProfileRequest } from "../types";

export const profileApi = {
    async getMyProfile(): Promise<MyProfile> {
        const response = await apiRequest<MyProfile>("/api/v1/profiles");
        return response.data;
    },

    async updateMyProfile(request: UpdateProfileRequest): Promise<MyProfile> {
        const response = await apiRequest<MyProfile>("/api/v1/profiles", { method: "PUT", body: request });
        return response.data;
    },

    async getProfileByUsername(username: string): Promise<PublicUserProfile> {
        const response = await apiRequest<PublicUserProfile>(`/api/v1/users/${encodeURIComponent(username)}`);
        return response.data;
    },

    async uploadAvatar(file: File): Promise<MyProfile> {
        const body = new FormData();
        body.append("file", file);
        const response = await apiRequest<MyProfile>("/api/v1/profiles/avatar", { method: "POST", body });
        return response.data;
    },

    async deleteAvatar(): Promise<void> {
        await apiRequest<void>("/api/v1/profiles/avatar", { method: "DELETE" });
    },
};
