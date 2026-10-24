export interface CommentAuthor {
    username: string;
    displayName: string;
    avatarUrl: string | null;
}

export interface Comment {
    id: string;
    author: CommentAuthor;
    content: string;
    parentCommentId: string | null;
    createdAt: string;
    updatedAt: string | null;
    replies: Comment[];
    /** Total replies under a top-level comment; `replies` holds only the first few until loaded in full. */
    replyCount: number;
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

export type ReplyTone = "NEUTRAL" | "FRIENDLY" | "PROFESSIONAL" | "SUPPORTIVE" | "HUMOROUS";

export const REPLY_TONES: { value: ReplyTone; label: string }[] = [
    { value: "NEUTRAL", label: "Neutral" },
    { value: "FRIENDLY", label: "Friendly" },
    { value: "PROFESSIONAL", label: "Professional" },
    { value: "SUPPORTIVE", label: "Supportive" },
    { value: "HUMOROUS", label: "Humorous" },
];
