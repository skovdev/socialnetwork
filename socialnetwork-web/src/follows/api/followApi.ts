import { apiRequest } from "../../core/api/httpClient";
import type { FollowSummary, FollowUserPage } from "../types";

export const followApi = {
    async follow(username: string): Promise<FollowSummary> {
        const response = await apiRequest<FollowSummary>(`/api/v1/users/${encodeURIComponent(username)}/follow`, {
            method: "POST",
        });
        return response.data;
    },

    async unfollow(username: string): Promise<FollowSummary> {
        const response = await apiRequest<FollowSummary>(`/api/v1/users/${encodeURIComponent(username)}/follow`, {
            method: "DELETE",
        });
        return response.data;
    },

    async getFollowers(username: string, page = 0, size = 20): Promise<FollowUserPage> {
        const response = await apiRequest<FollowUserPage>(
            `/api/v1/users/${encodeURIComponent(username)}/followers?page=${page}&size=${size}`,
        );
        return response.data;
    },

    async getFollowing(username: string, page = 0, size = 20): Promise<FollowUserPage> {
        const response = await apiRequest<FollowUserPage>(
            `/api/v1/users/${encodeURIComponent(username)}/following?page=${page}&size=${size}`,
        );
        return response.data;
    },
};
