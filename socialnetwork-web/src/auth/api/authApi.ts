import { apiRequest } from "../../core/api/httpClient";
import { tokenManager } from "../../core/api/tokenManager";

import type {
    ChangePasswordRequest,
    DeleteAccountRequest,
    LoginRequest,
    RegisterRequest,
    TokenResponse,
} from "../types";

export const authApi = {
    async register(request: RegisterRequest): Promise<void> {
        await apiRequest<void>("/api/v1/auth/register", { method: "POST", body: request, auth: false });
    },

    async verify(token: string): Promise<void> {
        await apiRequest<void>(`/api/v1/auth/verify?token=${encodeURIComponent(token)}`, { auth: false });
    },

    async resendVerification(email: string): Promise<void> {
        await apiRequest<void>("/api/v1/auth/resend-verification", {
            method: "POST",
            body: { email },
            auth: false,
        });
    },

    async login(request: LoginRequest): Promise<TokenResponse> {
        const response = await apiRequest<TokenResponse>("/api/v1/auth/login", {
            method: "POST",
            body: request,
            auth: false,
        });
        tokenManager.setTokens(response.data.accessToken, response.data.refreshToken);
        return response.data;
    },

    async refresh(): Promise<TokenResponse | null> {
        const refreshToken = tokenManager.getRefreshToken();
        if (!refreshToken) {
            return null;
        }
        try {
            const response = await apiRequest<TokenResponse>("/api/v1/auth/refresh", {
                method: "POST",
                body: { refreshToken },
                auth: false,
                skipRefresh: true,
            });
            tokenManager.setTokens(response.data.accessToken, response.data.refreshToken);
            return response.data;
        } catch {
            tokenManager.clear();
            return null;
        }
    },

    async logout(): Promise<void> {
        try {
            await apiRequest<void>("/api/v1/auth/logout", { method: "POST" });
        } finally {
            tokenManager.clear();
        }
    },

    async changePassword(request: ChangePasswordRequest): Promise<void> {
        await apiRequest<void>("/api/v1/auth/password", { method: "PUT", body: request });
    },

    async deleteAccount(request: DeleteAccountRequest): Promise<void> {
        await apiRequest<void>("/api/v1/auth/account", { method: "DELETE", body: request });
        tokenManager.clear();
    },
};
