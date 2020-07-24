package local.socialnetwork.core;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;

public class TestUtil {

    public static UUID randomUUID() {
        return UUID.randomUUID();
    }

    public static String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static boolean randomBoolean() {
        return new Random().nextBoolean();
    }
}