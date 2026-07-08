import { env } from "../config/env";
import { tokenManager } from "./tokenManager";

export interface ApiResponse<T> {
    timestamp: string;
    status: number;
    message: string;
    errorCode: string | null;
    data: T;
    errors: string[];
}

export class ApiError extends Error {
    readonly status: number;
    readonly errorCode: string | null;
    readonly errors: string[];

    constructor(message: string, status: number, errorCode: string | null, errors: string[]) {
        super(message);
        this.name = "ApiError";
        this.status = status;
        this.errorCode = errorCode;
        this.errors = errors;
    }
}

interface RequestOptions {
    method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
    body?: unknown;
    /** Attach the Authorization header. Defaults to true. */
    auth?: boolean;
    /** Skip the automatic refresh-and-retry on 401 (used by the refresh call itself). */
    skipRefresh?: boolean;
}

async function rawRequest<T>(path: string, options: RequestOptions): Promise<ApiResponse<T>> {
    const isFormData = options.body instanceof FormData;
    const headers: Record<string, string> = isFormData ? {} : { "Content-Type": "application/json" };
    if (options.auth !== false) {
        const accessToken = tokenManager.getAccessToken();
        if (accessToken) {
            headers.Authorization = `Bearer ${accessToken}`;
        }
    }

    const response = await fetch(`${env.apiBaseUrl}${path}`, {
        method: options.method ?? "GET",
        headers,
        body: options.body === undefined ? undefined : isFormData ? (options.body as FormData) : JSON.stringify(options.body),
    });

    if (response.status === 204) {
        return { timestamp: "", status: 204, message: "", errorCode: null, data: undefined as T, errors: [] };
    }

    const payload = (await response.json()) as ApiResponse<T>;

    if (!response.ok) {
        throw new ApiError(payload.message ?? "Request failed", response.status, payload.errorCode, payload.errors ?? []);
    }

    return payload;
}

async function refreshAccessToken(): Promise<boolean> {
    const refreshToken = tokenManager.getRefreshToken();
    if (!refreshToken) {
        return false;
    }

    try {
        const response = await rawRequest<{ accessToken: string; refreshToken: string }>("/api/v1/auth/refresh", {
            method: "POST",
            body: { refreshToken },
            auth: false,
            skipRefresh: true,
        });
        tokenManager.setTokens(response.data.accessToken, response.data.refreshToken);
        return true;
    } catch {
        tokenManager.clear();
        return false;
    }
}

/**
 * Performs an authenticated API request, transparently retrying once via the
 * refresh-token flow if the access token has expired (401).
 */
export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<ApiResponse<T>> {
    try {
        return await rawRequest<T>(path, options);
    } catch (error) {
        const isUnauthorized = error instanceof ApiError && error.status === 401;
        if (isUnauthorized && !options.skipRefresh && options.auth !== false) {
            const refreshed = await refreshAccessToken();
            if (refreshed) {
                return rawRequest<T>(path, options);
            }
        }
        throw error;
    }
}
