package serviceLayer;

import java.security.SecureRandom;

public class IdGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_ID_LENGTH = 8;

    // Generates a random alphanumeric ID of given length
    private static String generateRandomID(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    // Public methods for generating IDs of all types:
    public static String generateExpiryReportID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }

    public static String generateDiscountID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }

    public static String generateResID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }

    public static String generateTemplateID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }

    public static String generateSupplierID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }

    public static String generateAgreementID() {
        return generateRandomID(DEFAULT_ID_LENGTH);
    }
}
