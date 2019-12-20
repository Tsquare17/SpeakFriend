package com.tsquare.speakfriend.crypt

import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


object Crypt
{
    @JvmStatic
    fun encrypt(userId: Int, pass: String): String {
        return pass;
    }

    @JvmStatic
    fun generatePass(pass: String): String? {
        return generatePassword(pass)
    }

    @JvmStatic
    fun generateKey(key: String): String? {
        return key
    }

    @JvmStatic
    fun match(enteredPass: String, storedHash: String): Boolean {
        return validatePassword(enteredPass, storedHash);
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun generatePassword(password: String): String? {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = getSalt()
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded

        return iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getSalt(): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun validatePassword(originalPassword: String, storedPassword: String): Boolean {
        val parts = storedPassword.split(":".toRegex()).toTypedArray()
        val iterations = parts[0].toInt()
        val salt = fromHex(parts[1])
        val hash = fromHex(parts[2])
        val spec = PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.size * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val testHash = skf.generateSecret(spec).encoded
        var diff = hash.size xor testHash.size
        var i = 0
        while (i < hash.size && i < testHash.size) {
            diff = diff or hash[i].toInt() xor testHash[i].toInt()
            i++
        }

        return diff == 0
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }

        return bytes
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }
}
