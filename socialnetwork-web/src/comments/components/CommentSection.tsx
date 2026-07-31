import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";

import { commentApi } from "../api/commentApi";
import type { Comment } from "../types";
import { ApiError } from "../../core/api/httpClient";
import { Avatar } from "../../shared/components/Avatar";
import type { CurrentUser } from "../../shared/types";

function addReply(comments: Comment[], parentId: string, reply: Comment): Comment[] {
    return comments.map((comment) =>
        comment.id === parentId ? { ...comment, replies: [...comment.replies, reply] } : comment,
    );
}

function replaceComment(comments: Comment[], updated: Comment): Comment[] {
    return comments.map((comment) => {
        if (comment.id === updated.id) return { ...updated, replies: comment.replies };
        if (comment.replies.some((reply) => reply.id === updated.id)) {
            return { ...comment, replies: comment.replies.map((reply) => (reply.id === updated.id ? updated : reply)) };
        }
        return comment;
    });
}

function removeComment(comments: Comment[], id: string): Comment[] {
    return comments
        .filter((comment) => comment.id !== id)
        .map((comment) => ({ ...comment, replies: comment.replies.filter((reply) => reply.id !== id) }));
}

function formatTimestamp(iso: string): string {
    return new Date(iso).toLocaleString(undefined, {
        month: "short",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit",
    });
}

interface CommentFormProps {
    currentUser: CurrentUser | null;
    initialValue?: string;
    placeholder: string;
    submitLabel: string;
    onSubmit: (content: string) => Promise<void>;
    onCancel?: () => void;
}

function CommentForm({ currentUser, initialValue = "", placeholder, submitLabel, onSubmit, onCancel }: CommentFormProps) {
    const [content, setContent] = useState(initialValue);
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        if (!content.trim()) return;
        setIsSubmitting(true);
        try {
            await onSubmit(content);
            setContent("");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <form onSubmit={handleSubmit} className="comment-form-row">
            <Avatar avatarUrl={currentUser?.avatarUrl} displayName={currentUser?.displayName ?? ""} />
            <input
                type="text"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                maxLength={2000}
                placeholder={placeholder}
            />
            <button type="submit" className="comment-submit" disabled={isSubmitting || !content.trim()}>
                {isSubmitting ? "…" : submitLabel}
            </button>
            {onCancel && (
                <button type="button" className="comment-submit" onClick={onCancel}>
                    Cancel
                </button>
            )}
        </form>
    );
}

interface CommentCardProps {
    comment: Comment;
    currentUser: CurrentUser | null;
    isReply: boolean;
    onReply?: (content: string) => Promise<void>;
    onEdit: (content: string) => Promise<void>;
    onDelete: () => Promise<void>;
    /** Only used by top-level cards to render their nested replies. */
    onEditReply?: (replyId: string, content: string) => Promise<void>;
    onDeleteReply?: (replyId: string) => Promise<void>;
}

function CommentCard({
    comment,
    currentUser,
    isReply,
    onReply,
    onEdit,
    onDelete,
    onEditReply,
    onDeleteReply,
}: CommentCardProps) {
    const [isReplying, setIsReplying] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const isOwner = currentUser?.username === comment.author.username;

    return (
        <div className="comment-row">
            <Link to={`/users/${comment.author.username}`}>
                <Avatar avatarUrl={comment.author.avatarUrl} displayName={comment.author.displayName} />
            </Link>
            <div className="comment-body-col">
                {isEditing ? (
                    <CommentForm
                        currentUser={currentUser}
                        initialValue={comment.content}
                        placeholder="Edit your comment"
                        submitLabel="Save"
                        onCancel={() => setIsEditing(false)}
                        onSubmit={async (content) => {
                            await onEdit(content);
                            setIsEditing(false);
                        }}
                    />
                ) : (
                    <div className="comment-bubble">
                        <Link to={`/users/${comment.author.username}`} className="comment-author">
                            {comment.author.displayName}
                        </Link>
                        <span className="comment-text">{comment.content}</span>
                    </div>
                )}

                {!isEditing && (
                    <div className="comment-sub">
                        <span>{formatTimestamp(comment.createdAt)}</span>
                        {comment.updatedAt && <span>edited</span>}
                        {!isReply && onReply && (
                            <button type="button" onClick={() => setIsReplying((v) => !v)}>
                                Reply
                            </button>
                        )}
                        {isOwner && (
                            <>
                                <button type="button" onClick={() => setIsEditing(true)}>
                                    Edit
                                </button>
                                <button type="button" onClick={() => void onDelete()}>
                                    Delete
                                </button>
                            </>
                        )}
                    </div>
                )}

                {isReplying && onReply && (
                    <CommentForm
                        currentUser={currentUser}
                        placeholder={`Reply to @${comment.author.username}`}
                        submitLabel="Reply"
                        onCancel={() => setIsReplying(false)}
                        onSubmit={async (content) => {
                            await onReply(content);
                            setIsReplying(false);
                        }}
                    />
                )}

                {!isReply && comment.replies.length > 0 && (
                    <div className="replies">
                        {comment.replies.map((reply) => (
                            <CommentCard
                                key={reply.id}
                                comment={reply}
                                currentUser={currentUser}
                                isReply
                                onEdit={(content) => onEditReply!(reply.id, content)}
                                onDelete={() => onDeleteReply!(reply.id)}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

interface CommentSectionProps {
    postId: string;
    currentUser: CurrentUser | null;
}

export function CommentSection({ postId, currentUser }: CommentSectionProps) {
    const [comments, setComments] = useState<Comment[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setComments([]);
        setPage(0);
        setIsLoading(true);
        setError(null);
        commentApi
            .getComments(postId, 0)
            .then((loaded) => {
                setComments(loaded.content);
                setTotalPages(loaded.page.totalPages);
            })
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load comments"))
            .finally(() => setIsLoading(false));
    }, [postId]);

    async function handleLoadMore() {
        setIsLoadingMore(true);
        try {
            const next = page + 1;
            const loaded = await commentApi.getComments(postId, next);
            setComments((prev) => [...prev, ...loaded.content]);
            setPage(next);
            setTotalPages(loaded.page.totalPages);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to load comments");
        } finally {
            setIsLoadingMore(false);
        }
    }

    async function handleCreateComment(content: string) {
        const created = await commentApi.createComment(postId, { content });
        setComments((prev) => [...prev, created]);
    }

    async function handleCreateReply(parentId: string, content: string) {
        const created = await commentApi.createComment(postId, { content, parentCommentId: parentId });
        setComments((prev) => addReply(prev, parentId, created));
    }

    async function handleEditComment(id: string, content: string) {
        const updated = await commentApi.updateComment(id, { content });
        setComments((prev) => replaceComment(prev, updated));
    }

    async function handleDeleteComment(id: string) {
        await commentApi.deleteComment(id);
        setComments((prev) => removeComment(prev, id));
    }

    return (
        <section className="comments">
            {error && (
                <p className="alert" role="alert">
                    {error}
                </p>
            )}
            {isLoading && <p className="hint">Loading comments…</p>}
            {!isLoading && comments.length === 0 && !error && <p className="hint">No comments yet.</p>}

            {comments.map((comment) => (
                <CommentCard
                    key={comment.id}
                    comment={comment}
                    currentUser={currentUser}
                    isReply={false}
                    onReply={(content) => handleCreateReply(comment.id, content)}
                    onEdit={(content) => handleEditComment(comment.id, content)}
                    onDelete={() => handleDeleteComment(comment.id)}
                    onEditReply={handleEditComment}
                    onDeleteReply={handleDeleteComment}
                />
            ))}

            {page + 1 < totalPages && (
                <button className="view-more" type="button" onClick={() => void handleLoadMore()} disabled={isLoadingMore}>
                    {isLoadingMore ? "Loading…" : "View more comments"}
                </button>
            )}

            <CommentForm currentUser={currentUser} placeholder="Write a comment…" submitLabel="Post" onSubmit={handleCreateComment} />
        </section>
    );
}
