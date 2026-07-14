import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { postApi } from "../api/postApi";
import type { Post } from "../types";
import { profileApi } from "../../profiles/api/profileApi";
import { ApiError } from "../../core/api/httpClient";

export function PostDetailPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [post, setPost] = useState<Post | null>(null);
    const [myUsername, setMyUsername] = useState<string | null>(null);
    const [content, setContent] = useState("");
    const [isEditing, setIsEditing] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!id) return;
        setPost(null);
        setError(null);
        profileApi
            .getMyProfile()
            .then((profile) => setMyUsername(profile.username))
            .catch(() => {});
        postApi
            .getPost(id)
            .then((loaded) => {
                setPost(loaded);
                setContent(loaded.content);
            })
            .catch((err) =>
                setError(err instanceof ApiError && err.status === 404 ? "Post not found." : "Failed to load post"),
            );
    }, [id]);

    const isOwner = post !== null && myUsername === post.author.username;

    async function handleSave(event: FormEvent) {
        event.preventDefault();
        if (!id) return;
        setIsSubmitting(true);
        setError(null);
        try {
            const updated = await postApi.updatePost(id, { content });
            setPost(updated);
            setIsEditing(false);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to update post");
        } finally {
            setIsSubmitting(false);
        }
    }

    async function handleDelete() {
        if (!id) return;
        setError(null);
        try {
            await postApi.deletePost(id);
            navigate("/posts");
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
                <Link to="/posts" className="btn btn-secondary">
                    Back to feed
                </Link>
            </header>
            <main className="profile-main">
                {error && (
                    <p className="alert" role="alert">
                        {error}
                    </p>
                )}
                {!error && !post && <p className="hint">Loading…</p>}
                {!error && post && (
                    <div className="profile-card">
                        <div className="post-header">
                            <Link to={`/users/${post.author.username}`}>{post.author.displayName}</Link>
                            <span className="username">@{post.author.username}</span>
                        </div>

                        {isEditing ? (
                            <form onSubmit={handleSave}>
                                <label className="field">
                                    <span>Content</span>
                                    <textarea
                                        value={content}
                                        onChange={(e) => setContent(e.target.value)}
                                        rows={5}
                                        maxLength={5000}
                                        required
                                    />
                                </label>
                                <button type="submit" className="btn" disabled={isSubmitting}>
                                    {isSubmitting ? "Saving…" : "Save changes"}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => {
                                        setIsEditing(false);
                                        setContent(post.content);
                                    }}
                                >
                                    Cancel
                                </button>
                            </form>
                        ) : (
                            <>
                                <p className="post-content">{post.content}</p>
                                <div className="post-meta">
                                    <span>{new Date(post.createdAt).toLocaleString()}</span>
                                    {post.updatedAt && <span> · edited</span>}
                                </div>
                            </>
                        )}

                        {isOwner && !isEditing && (
                            <div className="post-actions">
                                <button type="button" className="btn-secondary" onClick={() => setIsEditing(true)}>
                                    Edit
                                </button>
                                <button type="button" className="btn-secondary" onClick={() => void handleDelete()}>
                                    Delete
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </main>
        </>
    );
}
