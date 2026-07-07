import { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "../hooks/AuthContext";
import { ApiError } from "../../core/api/httpClient";
import { AuthLayout } from "../../shared/components/AuthLayout";
import type { RegisterRequest } from "../types";

const emptyForm: RegisterRequest = {
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
    birthDate: "",
};

export function RegisterPage() {
    const { register } = useAuth();

    const [form, setForm] = useState<RegisterRequest>(emptyForm);
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isRegistered, setIsRegistered] = useState(false);

    function updateField<K extends keyof RegisterRequest>(field: K, value: RegisterRequest[K]) {
        setForm((prev) => ({ ...prev, [field]: value }));
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setError(null);
        setIsSubmitting(true);
        try {
            await register(form);
            setIsRegistered(true);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Registration failed");
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isRegistered) {
        return (
            <AuthLayout>
                <h1>Check your email</h1>
                <p className="hint">We sent a verification link to {form.email}.</p>
            </AuthLayout>
        );
    }

    return (
        <AuthLayout>
            <h1>Register</h1>
            <form onSubmit={handleSubmit}>
                {error && <p className="alert" role="alert">{error}</p>}
                <label className="field">
                    <span>First name</span>
                    <input
                        value={form.firstName}
                        onChange={(e) => updateField("firstName", e.target.value)}
                        required
                    />
                </label>
                <label className="field">
                    <span>Last name</span>
                    <input
                        value={form.lastName}
                        onChange={(e) => updateField("lastName", e.target.value)}
                        required
                    />
                </label>
                <label className="field">
                    <span>Username</span>
                    <input
                        value={form.username}
                        onChange={(e) => updateField("username", e.target.value)}
                        required
                    />
                </label>
                <label className="field">
                    <span>Email</span>
                    <input
                        type="email"
                        value={form.email}
                        onChange={(e) => updateField("email", e.target.value)}
                        required
                    />
                </label>
                <label className="field">
                    <span>Password</span>
                    <input
                        type="password"
                        value={form.password}
                        onChange={(e) => updateField("password", e.target.value)}
                        required
                    />
                </label>
                <label className="field">
                    <span>Date of birth</span>
                    <input
                        type="date"
                        value={form.birthDate}
                        onChange={(e) => updateField("birthDate", e.target.value)}
                        required
                    />
                </label>
                <button type="submit" className="btn" disabled={isSubmitting}>
                    {isSubmitting ? "Registering…" : "Register"}
                </button>
            </form>
            <p className="form-footer">
                Already have an account? <Link to="/login">Log in</Link>
            </p>
        </AuthLayout>
    );
}
