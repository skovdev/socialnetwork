package local.socialnetwork.profileservice.constants;

public final class AppConstants {

    public static final String HOST = "http://socialnetwork-zuul-api-gateway";
    public static final String PORT = "8762";

    private AppConstants() {

    }

    public static final class Services {

        public static final String USER_SERVICE_URL = "user-service";

        private Services() {

        }
    }
}