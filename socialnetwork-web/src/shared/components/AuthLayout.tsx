import type { ReactNode } from "react";

export function AuthLayout({ children }: { children: ReactNode }) {
    return (
        <div className="page-center">
            <div className="card">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                {children}
            </div>
        </div>
    );
}
