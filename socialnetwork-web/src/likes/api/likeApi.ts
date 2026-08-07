import { apiRequest } from "../../core/api/httpClient";
import type { LikeSummary } from "../types";

export const likeApi = {
    async likePost(postId: string): Promise<LikeSummary> {
        const response = await apiRequest<LikeSummary>(`/api/v1/posts/${encodeURIComponent(postId)}/likes`, { method: "POST" });
        return response.data;
    },

    async unlikePost(postId: string): Promise<LikeSummary> {
        const response = await apiRequest<LikeSummary>(`/api/v1/posts/${encodeURIComponent(postId)}/likes`, { method: "DELETE" });
        return response.data;
    },
};
