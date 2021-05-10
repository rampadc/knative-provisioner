package dev.congx.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomStringGenerator {
  // Random string generator: https://neilmadden.blog/2018/08/30/moving-away-from-uuids/
  private static final SecureRandom random = new SecureRandom();
  private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

  public static String generateRandomName() {
    byte[] buffer = new byte[20];
    random.nextBytes(buffer);
    return encoder.encodeToString(buffer);
  }
}
