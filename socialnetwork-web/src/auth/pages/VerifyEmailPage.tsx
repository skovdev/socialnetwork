import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";

import { authApi } from "../api/authApi";
import { ApiError } from "../../core/api/httpClient";

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
        return <p>Verifying your email…</p>;
    }

    if (state === "success") {
        return (
            <div>
                <h1>Email verified</h1>
                <p>
                    Your account is active. <Link to="/login">Log in</Link>
                </p>
            </div>
        );
    }

    return (
        <div>
            <h1>Verification failed</h1>
            <p role="alert">{error}</p>
        </div>
    );
}
