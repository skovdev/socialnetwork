export interface FollowSummary {
    followerCount: number;
    followingCount: number;
    followedByCurrentUser: boolean;
}

export interface FollowUser {
    id: string;
    user: {
        username: string;
        displayName: string;
        avatarUrl: string | null;
    };
    followedAt: string;
    followedByCurrentUser: boolean;
}

export interface FollowUserPage {
    content: FollowUser[];
    page: {
        totalPages: number;
        totalElements: number;
        number: number;
        size: number;
    };
}
