package local.socialnetwork.authserver.util;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public class UUIDUtil {

    private UUID getUUIDValueFromMap(String key, Map<String, Object> dataMap) {
        return Optional.ofNullable(dataMap.get(key))
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast)
                .orElse(null);
    }
}
