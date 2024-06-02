package local.socialnetwork.userservice.util;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UUIDUtil {

    public static UUID getUUIDValueFromMap(String key, Map<String, Object> dataMap) {
        return Optional.ofNullable(dataMap.get(key))
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast)
                .orElse(null);
    }
}
