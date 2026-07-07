import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";

import { useAuth } from "../../auth/hooks/AuthContext";

export function ProtectedRoute({ children }: { children: ReactNode }) {
    const { isAuthenticated, isInitializing } = useAuth();

    if (isInitializing) {
        return (
            <div className="page-center">
                <p className="hint">Loading…</p>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
}
