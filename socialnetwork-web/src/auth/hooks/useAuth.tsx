import { useEffect, useState } from "react";
import type { ReactNode } from "react";

import { authApi } from "../api/authApi";
import { tokenManager } from "../../core/api/tokenManager";
import { AuthContext } from "./AuthContext";
import type { LoginRequest, RegisterRequest } from "../types";

export function AuthProvider({ children }: { children: ReactNode }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isInitializing, setIsInitializing] = useState(true);

    useEffect(() => {
        let cancelled = false;

        async function bootstrap() {
            if (tokenManager.getRefreshToken()) {
                const result = await authApi.refresh();
                if (!cancelled) {
                    setIsAuthenticated(result !== null);
                }
            }
            if (!cancelled) {
                setIsInitializing(false);
            }
        }

        void bootstrap();
        return () => {
            cancelled = true;
        };
    }, []);

    async function login(request: LoginRequest) {
        await authApi.login(request);
        setIsAuthenticated(true);
    }

    async function register(request: RegisterRequest) {
        await authApi.register(request);
    }

    async function logout() {
        await authApi.logout();
        setIsAuthenticated(false);
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, isInitializing, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
