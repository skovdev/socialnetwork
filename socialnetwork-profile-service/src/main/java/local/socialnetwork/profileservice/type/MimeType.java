package local.socialnetwork.profileservice.type;

public enum MimeType {

    APPLICATION_JSON("application/json", "json"),
    APPLICATION_PDF("application/pdf", "pdf"),
    APPLICATION_XML("application/xml", "xml"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_JPEG("image/jpeg", "jpg"),
    IMAGE_PNG("image/png", "png"),
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css");

    private final String type;
    private final String format;

    MimeType(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }
}