import { useEffect, useRef, useState } from "react";
import type { FormEvent } from "react";

import { postApi } from "../api/postApi";
import type { Post } from "../types";
import { ApiError } from "../../core/api/httpClient";
import { PostCard } from "./PostCard";
import { Avatar } from "../../shared/components/Avatar";
import type { CurrentUser } from "../../shared/types";

interface PostListProps {
    username: string;
    currentUser: CurrentUser | null;
    showComposer: boolean;
}

export function PostList({ username, currentUser, showComposer }: PostListProps) {
    const [posts, setPosts] = useState<Post[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isComposing, setIsComposing] = useState(false);
    const [content, setContent] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [topic, setTopic] = useState("");
    const [isGenerating, setIsGenerating] = useState(false);
    const [generateError, setGenerateError] = useState<string | null>(null);
    const textareaRef = useRef<HTMLTextAreaElement>(null);

    useEffect(() => {
        setPosts([]);
        setPage(0);
        setIsLoading(true);
        setError(null);
        postApi
            .getPostsByUsername(username, 0)
            .then((loaded) => {
                setPosts(loaded.content);
                setTotalPages(loaded.page.totalPages);
            })
            .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load posts"))
            .finally(() => setIsLoading(false));
    }, [username]);

    useEffect(() => {
        if (isComposing) textareaRef.current?.focus();
    }, [isComposing]);

    async function handleLoadMore() {
        setIsLoadingMore(true);
        try {
            const next = page + 1;
            const loaded = await postApi.getPostsByUsername(username, next);
            setPosts((prev) => [...prev, ...loaded.content]);
            setPage(next);
            setTotalPages(loaded.page.totalPages);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to load posts");
        } finally {
            setIsLoadingMore(false);
        }
    }

    async function handleGenerate() {
        if (!topic.trim()) return;
        setIsGenerating(true);
        setGenerateError(null);
        try {
            const generated = await postApi.generatePostContent(topic);
            setContent(generated);
            textareaRef.current?.focus();
        } catch (err) {
            setGenerateError(err instanceof ApiError ? err.message : "Failed to generate post content");
        } finally {
            setIsGenerating(false);
        }
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        if (!content.trim()) return;
        setIsSubmitting(true);
        setError(null);
        try {
            const post = await postApi.createPost({ content });
            setPosts((prev) => [post, ...prev]);
            setContent("");
            setTopic("");
            setGenerateError(null);
            setIsComposing(false);
        } catch (err) {
            setError(err instanceof ApiError ? err.message : "Failed to create post");
        } finally {
            setIsSubmitting(false);
        }
    }

    function handleUpdated(updated: Post) {
        setPosts((prev) => prev.map((post) => (post.id === updated.id ? updated : post)));
    }

    function handleDeleted(id: string) {
        setPosts((prev) => prev.filter((post) => post.id !== id));
    }

    return (
        <>
            {showComposer && (
                <div className="composer">
                    {isComposing ? (
                        <form onSubmit={handleSubmit}>
                            <div className="composer-row">
                                <Avatar avatarUrl={currentUser?.avatarUrl} displayName={currentUser?.displayName ?? ""} />
                                <textarea
                                    ref={textareaRef}
                                    value={content}
                                    onChange={(e) => setContent(e.target.value)}
                                    rows={3}
                                    maxLength={5000}
                                    placeholder="What's on your mind?"
                                />
                            </div>
                            <div className="ai-generate-row">
                                <input
                                    type="text"
                                    className="ai-generate-input"
                                    value={topic}
                                    onChange={(e) => setTopic(e.target.value)}
                                    maxLength={300}
                                    placeholder="Give AI a topic or instruction…"
                                    disabled={isGenerating}
                                />
                                <button
                                    type="button"
                                    className="suggest-button"
                                    onClick={() => void handleGenerate()}
                                    disabled={isGenerating || !topic.trim()}
                                >
                                    {isGenerating ? "Generating…" : "✨ Generate with AI"}
                                </button>
                            </div>
                            {generateError && (
                                <p className="alert" role="alert">
                                    {generateError}
                                </p>
                            )}
                            <div className="composer-footer">
                                <span className="composer-hint">{content.length} / 5000</span>
                                <button
                                    type="button"
                                    className="btn-secondary"
                                    onClick={() => {
                                        setIsComposing(false);
                                        setContent("");
                                        setTopic("");
                                        setGenerateError(null);
                                    }}
                                >
                                    Cancel
                                </button>
                                <button type="submit" className="btn" disabled={isSubmitting || !content.trim()}>
                                    {isSubmitting ? "Posting…" : "Post"}
                                </button>
                            </div>
                        </form>
                    ) : (
                        <div className="composer-row">
                            <Avatar avatarUrl={currentUser?.avatarUrl} displayName={currentUser?.displayName ?? ""} />
                            <button type="button" className="composer-prompt" onClick={() => setIsComposing(true)}>
                                What&rsquo;s on your mind?
                            </button>
                        </div>
                    )}
                </div>
            )}

            {error && (
                <p className="alert" role="alert">
                    {error}
                </p>
            )}
            {isLoading && <p className="hint">Loading posts…</p>}
            {!isLoading && posts.length === 0 && <p className="hint">No posts yet.</p>}

            <div className="post-list">
                {posts.map((post) => (
                    <PostCard
                        key={post.id}
                        post={post}
                        currentUser={currentUser}
                        onUpdated={handleUpdated}
                        onDeleted={handleDeleted}
                    />
                ))}
            </div>

            {page + 1 < totalPages && (
                <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => void handleLoadMore()}
                    disabled={isLoadingMore}
                >
                    {isLoadingMore ? "Loading…" : "Load more posts"}
                </button>
            )}
        </>
    );
}
