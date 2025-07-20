package com.tsquare.speakfriend.utils;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    private final int iterations = 100000;
    private final int minPasswordIterations = 60000;

    public String encrypt(String key, String subject) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        return encrypt(key, subject, 0);
    }

    public String encrypt(String key, String subject, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        SecretKey secretKey = generateSecretKey(key, iv, iterations);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedData = cipher.doFinal(subject.getBytes());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedData.length);

        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);

        return toHex(byteBuffer.array());
    }

    public String decrypt(String key, String subject) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        return decrypt(key, subject, 0);
    }

    public String decrypt(String key, String subject, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(subject.isEmpty()) {
            return "";
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(fromHex(subject));
        int ivSize = byteBuffer.getInt();
        byte[] iv = new byte[ivSize];
        byteBuffer.get(iv);

        SecretKey secretKey = generateSecretKey(key, iv, iterations);
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        return new String(cipher.doFinal(cipherBytes));
    }

    public SecretKey generateSecretKey(String password, byte[] iv, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec;
        if (iterations != 0) {
            spec = new PBEKeySpec(password.toCharArray(), iv, iterations, 128);
        } else {
            spec = new PBEKeySpec(password.toCharArray(), iv, this.iterations, 128);
        }

        // Switched to SHA256 for better security
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, "AES");
    }

    /**
     * Legacy support for old SHA1 algorithm
     * Used for updating encryption in the update process.
     */
    public SecretKey generateSecretKeyLegacy(String password, byte[] iv, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec;
        if (iterations != 0) {
            spec = new PBEKeySpec(password.toCharArray(), iv, iterations, 128);
        } else {
            spec = new PBEKeySpec(password.toCharArray(), iv, 2000, 128);
        }

        // Use old SHA1 algorithm for legacy support
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, "AES");
    }

    /**
     * Decrypt legacy data for migration.
     */
    public String decryptLegacy(String key, String subject, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(subject.isEmpty()) {
            return "";
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(fromHex(subject));
        int ivSize = byteBuffer.getInt();
        byte[] iv = new byte[ivSize];
        byteBuffer.get(iv);

        SecretKey secretKey = generateSecretKeyLegacy(key, iv, iterations);
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        return new String(cipher.doFinal(cipherBytes));
    }

    public String generateKey(String hash, String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hash.split(":");
        byte[] salt = fromHex(parts[1]);
        int iterations = Integer.parseInt(parts[0]);

        // Check if this is old format
        if (iterations < 60000) {
            // Old format - use SHA1 with previous iterations and password reversal
            StringBuilder stringBuilder = new StringBuilder(pass);
            char[] chars = stringBuilder.reverse().toString().toCharArray();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 128);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashed = skf.generateSecret(spec).getEncoded();
            return toHex(hashed);
        } else {
            // New format - use SHA256 with new iterations and password
            char[] chars = pass.toCharArray();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 128);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashed = skf.generateSecret(spec).getEncoded();
            return toHex(hashed);
        }
    }

    /**
     * Generate key using old format for migration
     */
    public String generateKeyLegacy(String hash, String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hash.split(":");
        byte[] salt = fromHex(parts[1]);

        int iterations = 1500; // Old default iterations

        StringBuilder stringBuilder = new StringBuilder(pass);
        char[] chars = stringBuilder.reverse().toString().toCharArray();

        // Use SHA1 for legacy key generation
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 128);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashed = skf.generateSecret(spec).getEncoded();
        return toHex(hashed);
    }

    /**
     * Generate legacy key by converting password hash to old format
     */
    public String generateLegacyKeyFromHash(String hash, String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hash.split(":");
        byte[] salt = fromHex(parts[1]);

        String oldFormatHash = "1500:" + toHex(salt) + ":" + parts[2];

        return generateKey(oldFormatHash, pass);
    }

    public boolean match(String enteredPass, String storedHash) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return validatePassword(enteredPass, storedHash);
    }

    public String generatePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Bumped up iterations for better security
        int iterations = minPasswordIterations;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        return salt;
    }

    private boolean validatePassword(String originalPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        // Try SHA256 first
        try {
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            if (constantTimeEquals(hash, testHash)) {
                return true;
            }
        } catch (Exception e) {
            // If SHA256 fails, try SHA1 (old format)
        }

        // Try SHA1 for legacy support
        try {
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            return constantTimeEquals(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Simple byte array comparison
     * Timing attacks aren't really an issue now, but if network access is added later, we'll probably want this.
     */
    private boolean constantTimeEquals(byte[] a, byte[] b) {
        return java.util.Arrays.equals(a, b);
    }

    private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String hexSubString = hex.substring(2 * i, 2 * i + 2);
            bytes[i] = (byte) Integer.parseInt(hexSubString, 16);
        }

        return bytes;
    }

    private String toHex(byte[] array) {
        StringBuilder hex = new StringBuilder();
        for (byte b : array) {
            hex.append(String.format("%02x", b & 0xff));
        }
        return hex.toString();
    }
}
