import { Navigate, Route, Routes } from "react-router-dom";

import { LoginPage } from "../../auth/pages/LoginPage";
import { RegisterPage } from "../../auth/pages/RegisterPage";
import { VerifyEmailPage } from "../../auth/pages/VerifyEmailPage";
import { EditProfilePage } from "../../profiles/pages/EditProfilePage";
import { ProfilePage } from "../../profiles/pages/ProfilePage";
import { UserProfilePage } from "../../profiles/pages/UserProfilePage";
import { FeedPage } from "../../posts/pages/FeedPage";
import { PostDetailPage } from "../../posts/pages/PostDetailPage";
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
            <Route
                path="/profile/edit"
                element={
                    <ProtectedRoute>
                        <EditProfilePage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/users/:username"
                element={
                    <ProtectedRoute>
                        <UserProfilePage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/posts"
                element={
                    <ProtectedRoute>
                        <FeedPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/posts/:id"
                element={
                    <ProtectedRoute>
                        <PostDetailPage />
                    </ProtectedRoute>
                }
            />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}
