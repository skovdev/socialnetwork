import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";

import { useAuth } from "../../auth/hooks/AuthContext";

export function ProtectedRoute({ children }: { children: ReactNode }) {
    const { isAuthenticated, isInitializing } = useAuth();

    if (isInitializing) {
        return <p>Loading…</p>;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
}
