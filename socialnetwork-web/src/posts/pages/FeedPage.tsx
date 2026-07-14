import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";

import { postApi } from "../api/postApi";
import type { Post } from "../types";
import { profileApi } from "../../profiles/api/profileApi";
import { ApiError } from "../../core/api/httpClient";
import { useAuth } from "../../auth/hooks/AuthContext";

export function FeedPage() {
    const { logout } = useAuth();

    const [posts, setPosts] = useState<Post[]>([]);
    const [myUsername, setMyUsername] = useState<string | null>(null);
    const [content, setContent] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        profileApi
            .getMyProfile()
            .then((profile) => setMyUsername(profile.username))
            .catch(() => {});
        postApi
            .getFeed()
            .then((page) => setPosts(page.content))
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load feed"))
            .finally(() => setIsLoading(false));
    }, []);

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        if (!content.trim()) return;
        setIsSubmitting(true);
        setError(null);
        try {
            const post = await postApi.createPost({ content });
            setPosts((prev) => [post, ...prev]);
            setContent("");
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to create post");
        } finally {
            setIsSubmitting(false);
        }
    }

    async function handleDelete(id: string) {
        setError(null);
        try {
            await postApi.deletePost(id);
            setPosts((prev) => prev.filter((post) => post.id !== id));
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to delete post");
        }
    }

    return (
        <>
            <header className="topbar">
                <div className="brand">
                    <span className="brand-mark">S</span>
                    <span className="brand-name">SocialNetwork</span>
                </div>
                <div>
                    <Link to="/profile" className="btn btn-secondary">
                        My profile
                    </Link>
                    <button type="button" className="btn btn-secondary" onClick={() => void logout()}>
                        Log out
                    </button>
                </div>
            </header>
            <main className="profile-main">
                <div className="profile-card post-composer">
                    <form onSubmit={handleSubmit}>
                        <label className="field">
                            <span>Share something</span>
                            <textarea
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                rows={3}
                                maxLength={5000}
                                placeholder="What's on your mind?"
                            />
                        </label>
                        <button type="submit" className="btn" disabled={isSubmitting || !content.trim()}>
                            {isSubmitting ? "Posting…" : "Post"}
                        </button>
                    </form>
                </div>

                {error && (
                    <p className="alert" role="alert">
                        {error}
                    </p>
                )}
                {isLoading && <p className="hint">Loading…</p>}
                {!isLoading && posts.length === 0 && <p className="hint">No posts yet.</p>}

                <div className="post-list">
                    {posts.map((post) => (
                        <article key={post.id} className="post-card">
                            <div className="post-header">
                                <Link to={`/users/${post.author.username}`}>{post.author.displayName}</Link>
                                <span className="username">@{post.author.username}</span>
                            </div>
                            <p className="post-content">{post.content}</p>
                            <div className="post-meta">
                                <span>{new Date(post.createdAt).toLocaleString()}</span>
                                {post.updatedAt && <span> · edited</span>}
                            </div>
                            {myUsername === post.author.username && (
                                <div className="post-actions">
                                    <Link to={`/posts/${post.id}`} className="btn-secondary">
                                        Edit
                                    </Link>
                                    <button type="button" className="btn-secondary" onClick={() => void handleDelete(post.id)}>
                                        Delete
                                    </button>
                                </div>
                            )}
                        </article>
                    ))}
                </div>
            </main>
        </>
    );
}
