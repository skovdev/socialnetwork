import { apiRequest } from "../../core/api/httpClient";
import type { CreatePostRequest, Post, PostPage, UpdatePostRequest } from "../types";

export const postApi = {
    async createPost(request: CreatePostRequest): Promise<Post> {
        const response = await apiRequest<Post>("/api/v1/posts", { method: "POST", body: request });
        return response.data;
    },

    async getFeed(page = 0, size = 20): Promise<PostPage> {
        const response = await apiRequest<PostPage>(`/api/v1/posts?page=${page}&size=${size}`);
        return response.data;
    },

    async getPost(id: string): Promise<Post> {
        const response = await apiRequest<Post>(`/api/v1/posts/${encodeURIComponent(id)}`);
        return response.data;
    },

    async updatePost(id: string, request: UpdatePostRequest): Promise<Post> {
        const response = await apiRequest<Post>(`/api/v1/posts/${encodeURIComponent(id)}`, { method: "PUT", body: request });
        return response.data;
    },

    async deletePost(id: string): Promise<void> {
        await apiRequest<void>(`/api/v1/posts/${encodeURIComponent(id)}`, { method: "DELETE" });
    },
};
