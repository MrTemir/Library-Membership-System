package other;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Cipher {

    /**
     * Hashes data using the specified algorithm.
     *
     * @param data      Data to be hashed.
     * @param algorithm Hashing algorithm (e.g., "SHA-256", "MD5").
     * @return Hashed data as a string (hex).
     * @throws NoSuchAlgorithmException If the specified algorithm is not supported.
     */
    public String hashData(String data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Hashes data using the SHA-256 algorithm.
     *
     * @param data Data to be hashed.
     * @return Hashed data as a string (hex).
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not supported.
     */
    public String hashWithSHA256(String data) throws NoSuchAlgorithmException {
        return hashData(data, "SHA-256");
    }

    /**
     * Hashes data using the MD5 algorithm.
     *
     * @param data Data to be hashed.
     * @return Hashed data as a string (hex).
     * @throws NoSuchAlgorithmException If the MD5 algorithm is not supported.
     */
    public String hashWithMD5(String data) throws NoSuchAlgorithmException {
        return hashData(data, "MD5");
    }

    /**
     * Generates a unique ID for a new member.
     * The ID consists of 4 digits and 2 uppercase letters.
     *
     * @return Generated ID.
     */
    public String generateMemberId() {
        Random random = new Random();

        // Generate 4 digits
        int numberPart = 1000 + random.nextInt(9000); // Range from 1000 to 9999

        // Generate 2 uppercase letters
        char letter1 = (char) ('A' + random.nextInt(26)); // First letter
        char letter2 = (char) ('A' + random.nextInt(26)); // Second letter

        // Combine parts into one ID
        return numberPart + "" + letter1 + letter2;
    }

    /**
     * Checks the strength of a password.
     * The password must:
     * - Be at least 8 characters long.
     * - Contain at least one digit (0-9).
     * - Contain at least one uppercase letter (A-R).
     * - Contain at least one special character.
     *
     * @param password The password to check.
     * @return True if the password is strong, false otherwise.
     */
    public boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false; // Password is too short
        }

        boolean hasDigit = false;
        boolean hasUppercaseAR = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (c >= 'A' && c <= 'R') {
                hasUppercaseAR = true;
            } else if ("!@#$%^&*()_+-=[]{}|;:'\",.<>?/".indexOf(c) >= 0) {
                hasSpecialChar = true;
            }

            // If all conditions are met, no need to continue checking
            if (hasDigit && hasUppercaseAR && hasSpecialChar) {
                return true;
            }
        }

        return false; // If any condition is not met
    }
}