import { useEffect, useRef, useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";

import { postApi } from "../api/postApi";
import type { Post } from "../types";
import { ApiError } from "../../core/api/httpClient";
import { CommentSection } from "../../comments/components/CommentSection";
import { Avatar } from "../../shared/components/Avatar";
import type { CurrentUser } from "../../shared/types";

interface PostCardProps {
    post: Post;
    currentUser: CurrentUser | null;
    onUpdated: (post: Post) => void;
    onDeleted: (id: string) => void;
}

function formatTimestamp(iso: string): string {
    return new Date(iso).toLocaleString(undefined, {
        month: "short",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit",
    });
}

export function PostCard({ post, currentUser, onUpdated, onDeleted }: PostCardProps) {
    const [content, setContent] = useState(post.content);
    const [isEditing, setIsEditing] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [showComments, setShowComments] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const menuRef = useRef<HTMLDivElement>(null);

    const isOwner = currentUser?.username === post.author.username;

    useEffect(() => {
        if (!menuOpen) return;
        function handleClickOutside(event: MouseEvent) {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setMenuOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [menuOpen]);

    async function handleSave(event: FormEvent) {
        event.preventDefault();
        setIsSubmitting(true);
        setError(null);
        try {
            const updated = await postApi.updatePost(post.id, { content });
            onUpdated(updated);
            setIsEditing(false);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to update post");
        } finally {
            setIsSubmitting(false);
        }
    }

    async function handleDelete() {
        setError(null);
        setMenuOpen(false);
        try {
            await postApi.deletePost(post.id);
            onDeleted(post.id);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to delete post");
        }
    }

    return (
        <article className="post-card">
            <div className="post-head">
                <Link to={`/users/${post.author.username}`}>
                    <Avatar avatarUrl={post.author.avatarUrl} displayName={post.author.displayName} />
                </Link>
                <div className="post-authorline">
                    <Link to={`/users/${post.author.username}`} className="post-author">
                        {post.author.displayName}
                    </Link>
                    <div className="post-time">
                        {formatTimestamp(post.createdAt)}
                        {post.updatedAt && " · edited"}
                    </div>
                </div>
                {isOwner && (
                    <div className="menu-wrap" ref={menuRef}>
                        <button
                            type="button"
                            className="kebab"
                            aria-label="Post options"
                            onClick={() => setMenuOpen((v) => !v)}
                        >
                            &#8942;
                        </button>
                        <div className={menuOpen ? "menu open" : "menu"}>
                            <button
                                type="button"
                                onClick={() => {
                                    setMenuOpen(false);
                                    setIsEditing(true);
                                }}
                            >
                                Edit post
                            </button>
                            <button type="button" className="danger" onClick={() => void handleDelete()}>
                                Delete post
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {error && (
                <p className="alert" role="alert">
                    {error}
                </p>
            )}

            {isEditing ? (
                <form onSubmit={handleSave}>
                    <label className="field">
                        <span>Content</span>
                        <textarea
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            rows={4}
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
                <p className="post-content">{post.content}</p>
            )}

            {!isEditing && (
                <div className="post-actions">
                    <button
                        type="button"
                        className={showComments ? "action-btn active" : "action-btn"}
                        onClick={() => setShowComments((v) => !v)}
                    >
                        <svg viewBox="0 0 24 24">
                            <path d="M4 5h16v11H8l-4 4V5z" />
                        </svg>
                        {showComments ? "Hide comments" : "Comment"}
                    </button>
                </div>
            )}

            {showComments && <CommentSection postId={post.id} currentUser={currentUser} />}
        </article>
    );
}
