import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../hooks/AuthContext";
import { ApiError } from "../../core/api/httpClient";
import { AuthLayout } from "../../shared/components/AuthLayout";

export function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setError(null);
        setIsSubmitting(true);
        try {
            await login({ username, password });
            navigate("/profile");
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Login failed");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <AuthLayout>
            <h1>Log in</h1>
            <form onSubmit={handleSubmit}>
                {error && <p className="alert" role="alert">{error}</p>}
                <label className="field">
                    <span>Username</span>
                    <input value={username} onChange={(e) => setUsername(e.target.value)} required />
                </label>
                <label className="field">
                    <span>Password</span>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </label>
                <button type="submit" className="btn" disabled={isSubmitting}>
                    {isSubmitting ? "Logging in…" : "Log in"}
                </button>
            </form>
            <p className="form-footer">
                No account? <Link to="/register">Register</Link>
            </p>
        </AuthLayout>
    );
}
