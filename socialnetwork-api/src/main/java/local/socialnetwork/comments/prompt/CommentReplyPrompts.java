package local.socialnetwork.comments.prompt;

import local.socialnetwork.comments.dto.ReplyTone;

/**
 * System-prompt constants for comment-reply-suggestion AI generation. Centralising the prompt
 * here keeps AI wording out of {@link local.socialnetwork.comments.service.impl.CommentReplySuggestionServiceImpl}.
 */
public interface CommentReplyPrompts {

    /**
     * System prompt template that instructs the model to produce exactly 3 distinct reply
     * suggestions for a comment, in the desired tone. {@code %s} is replaced with a tone
     * descriptor (see {@link ReplyTone#promptDescriptor()}).
     */
    String SYSTEM_PROMPT_TEMPLATE = """
            You are a helpful assistant that writes reply suggestions for comments on a social network.
            You will be given the post the comment was made on, optional prior thread context, and the specific comment to reply to.
            Generate exactly 3 concise, natural, and clearly different reply suggestions for that comment.
            Base every suggestion strictly on the post and comment text provided below. Do not invent facts, \
            names, opinions, or any details that are not present in that context. If the context is not \
            enough to say something specific, keep the suggestions brief, generic, and non-committal.
            Each suggestion must be a single short sentence (at most two sentences), written in a %s tone, and \
            directly relevant to the comment and post context.
            Output only a numbered list with exactly 3 lines, in this exact format:
            1. <suggestion one>
            2. <suggestion two>
            3. <suggestion three>
            Do not include explanations, commentary, headings, or any text besides the 3 numbered suggestions.""";

    static String systemPrompt(ReplyTone tone) {
        return SYSTEM_PROMPT_TEMPLATE.formatted(tone.promptDescriptor());
    }
}
