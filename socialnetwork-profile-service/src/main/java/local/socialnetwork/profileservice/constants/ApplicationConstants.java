package local.socialnetwork.profileservice.constants;

public final class ApplicationConstants {

    public static final String HOST = "http://socialnetwork-zuul-api-gateway";
    public static final String PORT = "8762";

    private ApplicationConstants() {

    }

    public static final class Services {

        public static final String USER_SERVICE_URL = "user-service";

        private Services() {

        }
    }
}