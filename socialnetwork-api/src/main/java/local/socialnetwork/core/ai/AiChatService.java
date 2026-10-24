package local.socialnetwork.core.ai;

import java.util.List;

/**
 * Provider-agnostic AI chat service. Sends a system prompt and a user prompt to the configured
 * AI model and returns the generated text response.
 */
public interface AiChatService {

    /**
     * Sends the given prompts to the AI model and returns the generated text.
     *
     * @param systemPrompt instruction that sets the model's behaviour and persona
     * @param userPrompt   the user's request or context to act on
     * @return the AI-generated text response
     * @throws local.socialnetwork.shared.exception.AiChatException if the underlying AI model call fails
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * Like {@link #chat} but asks the model for a JSON array of strings and parses it, so callers never have to
     * guess at free-text formatting.
     *
     * @return the non-blank items of the model's list, trimmed
     * @throws local.socialnetwork.shared.exception.AiChatException if the call fails or the response is not a usable list
     */
    List<String> chatList(String systemPrompt, String userPrompt);
}
