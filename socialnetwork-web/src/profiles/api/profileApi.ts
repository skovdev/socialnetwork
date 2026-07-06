import { apiRequest } from "../../core/api/httpClient";
import type { UpdateProfileRequest, UserProfile } from "../types";

export const profileApi = {
    async getMyProfile(): Promise<UserProfile> {
        const response = await apiRequest<UserProfile>("/api/v1/profiles");
        return response.data;
    },

    async updateMyProfile(request: UpdateProfileRequest): Promise<UserProfile> {
        const response = await apiRequest<UserProfile>("/api/v1/profiles", { method: "PUT", body: request });
        return response.data;
    },

    async getProfileByUsername(username: string): Promise<UserProfile> {
        const response = await apiRequest<UserProfile>(`/api/v1/users/${encodeURIComponent(username)}`);
        return response.data;
    },
};
