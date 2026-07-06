import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../hooks/AuthContext";
import { ApiError } from "../../core/api/httpClient";

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
        <form onSubmit={handleSubmit}>
            <h1>Log in</h1>
            {error && <p role="alert">{error}</p>}
            <label>
                Username
                <input value={username} onChange={(e) => setUsername(e.target.value)} required />
            </label>
            <label>
                Password
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </label>
            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Logging in…" : "Log in"}
            </button>
            <p>
                No account? <Link to="/register">Register</Link>
            </p>
        </form>
    );
}
