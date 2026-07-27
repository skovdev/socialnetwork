export interface CommentAuthor {
    username: string;
    displayName: string;
}

export interface Comment {
    id: string;
    author: CommentAuthor;
    content: string;
    parentCommentId: string | null;
    createdAt: string;
    updatedAt: string | null;
    replies: Comment[];
}

export interface CreateCommentRequest {
    content: string;
    parentCommentId?: string | null;
}

export interface UpdateCommentRequest {
    content: string;
}

export interface CommentPage {
    content: Comment[];
    page: {
        totalPages: number;
        totalElements: number;
        number: number;
        size: number;
    };
}
