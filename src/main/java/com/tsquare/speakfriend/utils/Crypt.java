package com.tsquare.speakfriend.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    private final int iterations = 2000;

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

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(key, "AES");
    }

    public String generateKey(String hash, String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hash.split(":");
        byte[] salt = fromHex(parts[1]);

        StringBuilder stringBuilder = new StringBuilder(pass);
        char[] chars = stringBuilder.reverse().toString().toCharArray();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, 1500, 128);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashed = skf.generateSecret(spec).getEncoded();

        return toHex(hashed);
    }

    public boolean match(String enteredPass, String storedHash) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return validatePassword(enteredPass, storedHash);
    }

    public String generatePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        return salt;
    }

    private boolean validatePassword(String originalPassword, String storedPassword) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
        int diff = hash.length ^ testHash.length;
        int i = 0;
        while (i < hash.length && i < testHash.length) {
            diff = (diff | hash[i]) ^ testHash[i];
            i++;
        }

        return diff == 0;
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
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = array.length * 2 - hex.length();

        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }

        return hex;
    }
}
