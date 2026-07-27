import { apiRequest } from "../../core/api/httpClient";
import type { Comment, CommentPage, CreateCommentRequest, UpdateCommentRequest } from "../types";

export const commentApi = {
    async createComment(postId: string, request: CreateCommentRequest): Promise<Comment> {
        const response = await apiRequest<Comment>(`/api/v1/posts/${encodeURIComponent(postId)}/comments`, {
            method: "POST",
            body: request,
        });
        return response.data;
    },

    async getComments(postId: string, page = 0, size = 20): Promise<CommentPage> {
        const response = await apiRequest<CommentPage>(
            `/api/v1/posts/${encodeURIComponent(postId)}/comments?page=${page}&size=${size}`,
        );
        return response.data;
    },

    async updateComment(id: string, request: UpdateCommentRequest): Promise<Comment> {
        const response = await apiRequest<Comment>(`/api/v1/comments/${encodeURIComponent(id)}`, {
            method: "PUT",
            body: request,
        });
        return response.data;
    },

    async deleteComment(id: string): Promise<void> {
        await apiRequest<void>(`/api/v1/comments/${encodeURIComponent(id)}`, { method: "DELETE" });
    },
};
