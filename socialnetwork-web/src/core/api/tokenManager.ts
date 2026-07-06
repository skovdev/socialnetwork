const REFRESH_TOKEN_STORAGE_KEY = "socialnetwork.refreshToken";

let accessToken: string | null = null;

export const tokenManager = {
    getAccessToken(): string | null {
        return accessToken;
    },

    getRefreshToken(): string | null {
        return localStorage.getItem(REFRESH_TOKEN_STORAGE_KEY);
    },

    setTokens(newAccessToken: string, newRefreshToken: string): void {
        accessToken = newAccessToken;
        localStorage.setItem(REFRESH_TOKEN_STORAGE_KEY, newRefreshToken);
    },

    clear(): void {
        accessToken = null;
        localStorage.removeItem(REFRESH_TOKEN_STORAGE_KEY);
    },
};
