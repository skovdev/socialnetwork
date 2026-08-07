export interface PostAuthor {
    username: string;
    displayName: string;
    avatarUrl: string | null;
}

export interface Post {
    id: string;
    author: PostAuthor;
    content: string;
    createdAt: string;
    updatedAt: string | null;
    likeCount: number;
    likedByCurrentUser: boolean;
}

export interface CreatePostRequest {
    content: string;
}

export interface UpdatePostRequest {
    content: string;
}

export interface PostPage {
    content: Post[];
    page: {
        totalPages: number;
        totalElements: number;
        number: number;
        size: number;
    };
}
