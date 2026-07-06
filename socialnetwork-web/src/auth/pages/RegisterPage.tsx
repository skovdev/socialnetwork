import { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "../hooks/AuthContext";
import { ApiError } from "../../core/api/httpClient";
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
            <div>
                <h1>Check your email</h1>
                <p>We sent a verification link to {form.email}.</p>
            </div>
        );
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Register</h1>
            {error && <p role="alert">{error}</p>}
            <label>
                First name
                <input value={form.firstName} onChange={(e) => updateField("firstName", e.target.value)} required />
            </label>
            <label>
                Last name
                <input value={form.lastName} onChange={(e) => updateField("lastName", e.target.value)} required />
            </label>
            <label>
                Username
                <input value={form.username} onChange={(e) => updateField("username", e.target.value)} required />
            </label>
            <label>
                Email
                <input
                    type="email"
                    value={form.email}
                    onChange={(e) => updateField("email", e.target.value)}
                    required
                />
            </label>
            <label>
                Password
                <input
                    type="password"
                    value={form.password}
                    onChange={(e) => updateField("password", e.target.value)}
                    required
                />
            </label>
            <label>
                Date of birth
                <input
                    type="date"
                    value={form.birthDate}
                    onChange={(e) => updateField("birthDate", e.target.value)}
                    required
                />
            </label>
            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Registering…" : "Register"}
            </button>
            <p>
                Already have an account? <Link to="/login">Log in</Link>
            </p>
        </form>
    );
}
