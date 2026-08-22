package local.socialnetwork.comments.dto;

/**
 * Desired tone for an AI-generated comment reply suggestion.
 */
public enum ReplyTone {

    NEUTRAL("neutral and balanced"),
    FRIENDLY("friendly and warm"),
    PROFESSIONAL("professional and formal"),
    SUPPORTIVE("supportive and empathetic"),
    HUMOROUS("lighthearted and a little humorous");

    private final String promptDescriptor;

    ReplyTone(String promptDescriptor) {
        this.promptDescriptor = promptDescriptor;
    }

    /**
     * A short natural-language description of this tone, suitable for inclusion in an AI prompt.
     */
    public String promptDescriptor() {
        return promptDescriptor;
    }
}
