import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";

import { authApi } from "../api/authApi";
import { ApiError } from "../../core/api/httpClient";
import { AuthLayout } from "../../shared/components/AuthLayout";

type VerificationState = "verifying" | "success" | "error";

export function VerifyEmailPage() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");

    const [state, setState] = useState<VerificationState>("verifying");
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!token) {
            setState("error");
            setError("Missing verification token");
            return;
        }

        authApi
            .verify(token)
            .then(() => setState("success"))
            .catch((err) => {
                setState("error");
                setError(err instanceof ApiError ? err.message : "Verification failed");
            });
    }, [token]);

    if (state === "verifying") {
        return (
            <AuthLayout>
                <p className="hint">Verifying your email…</p>
            </AuthLayout>
        );
    }

    if (state === "success") {
        return (
            <AuthLayout>
                <h1>Email verified</h1>
                <p className="form-footer">
                    Your account is active. <Link to="/login">Log in</Link>
                </p>
            </AuthLayout>
        );
    }

    return (
        <AuthLayout>
            <h1>Verification failed</h1>
            <p className="alert" role="alert">
                {error}
            </p>
        </AuthLayout>
    );
}
