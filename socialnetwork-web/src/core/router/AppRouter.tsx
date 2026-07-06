import { Navigate, Route, Routes } from "react-router-dom";

import { LoginPage } from "../../auth/pages/LoginPage";
import { RegisterPage } from "../../auth/pages/RegisterPage";
import { VerifyEmailPage } from "../../auth/pages/VerifyEmailPage";
import { ProfilePage } from "../../profiles/pages/ProfilePage";
import { ProtectedRoute } from "../../shared/components/ProtectedRoute";

export function AppRouter() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/verify" element={<VerifyEmailPage />} />
            <Route
                path="/profile"
                element={
                    <ProtectedRoute>
                        <ProfilePage />
                    </ProtectedRoute>
                }
            />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}
