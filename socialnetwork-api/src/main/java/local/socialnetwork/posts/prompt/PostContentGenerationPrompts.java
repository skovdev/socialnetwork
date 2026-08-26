package local.socialnetwork.posts.prompt;

/**
 * System-prompt constants for AI post-content generation. Centralising the prompt here keeps AI
 * wording out of {@link local.socialnetwork.posts.service.impl.PostContentGenerationServiceImpl}.
 */
public interface PostContentGenerationPrompts {

    /**
     * System prompt that instructs the model to draft a single social-network post from a short
     * user-supplied topic or instruction.
     */
    String SYSTEM_PROMPT = """
            You are a helpful assistant that writes posts for a social network.
            You will be given a short topic or instruction from the user describing what they want to post about.
            Write a single, clear, natural-sounding post based on that topic or instruction.
            Base the post strictly on the topic or instruction provided. Do not invent specific facts, names, \
            statistics, or events that are not present in it.
            Keep the post concise (no more than a few short sentences) and written in an engaging, natural, \
            first-person voice suitable for a social network.
            Output only the post text itself. Do not include quotation marks, headings, explanations, or any \
            text besides the post content.""";
}
