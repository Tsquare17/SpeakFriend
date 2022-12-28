package com.tsquare.speakfriend.crypt

import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object Crypt
{
    private const val iterations: Int = 2000

    @JvmStatic
    @JvmOverloads
    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class,
        InvalidKeyException::class, InvalidKeySpecException::class,
        NoSuchPaddingException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    fun encrypt(key: String, subject: String, iterations: Int = 0): String {
        val secureRandom = SecureRandom()
        val iv = ByteArray(12)
        secureRandom.nextBytes(iv)

        val secretKey = generateSecretKey(key, iv, iterations)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val parameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        val encryptedData = cipher.doFinal(subject.toByteArray())
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(4 + iv.size + encryptedData.size)

        byteBuffer.putInt(iv.size)
        byteBuffer.put(iv)
        byteBuffer.put(encryptedData)

        return toHex(byteBuffer.array())
    }

    @JvmStatic
    @JvmOverloads
    fun decrypt(key: String, subject: String?, iterations: Int = 0): String {
        if(subject === null) {
            return "";
        }

        val byteBuffer = ByteBuffer.wrap(fromHex(subject))
        val ivSize = byteBuffer.int
        val iv = ByteArray(ivSize)
        byteBuffer[iv]

        val secretKey = generateSecretKey(key, iv, iterations)
        val cipherBytes = ByteArray(byteBuffer.remaining())
        byteBuffer[cipherBytes]

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val parameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        return String(cipher.doFinal(cipherBytes))
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateSecretKey(password: String, iv: ByteArray?, iterations: Int = 0): SecretKey? {
        val spec: KeySpec = if (iterations != 0) {
            PBEKeySpec(password.toCharArray(), iv, iterations, 128)
        } else {
            PBEKeySpec(password.toCharArray(), iv, this.iterations, 128)
        }

        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val key = secretKeyFactory.generateSecret(spec).encoded
        return SecretKeySpec(key, "AES")
    }

    @JvmStatic
    fun generateKey(hash: String, pass: String): String? {
        val parts = hash.split(":".toRegex()).toTypedArray()
        val salt = fromHex(parts[1])
        val chars = pass.reversed().toCharArray()
        val spec = PBEKeySpec(chars, salt, 1500, 128)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hashed = skf.generateSecret(spec).encoded

        return toHex(hashed)
    }

    @JvmStatic
    fun match(enteredPass: String, storedHash: String): Boolean {
        return validatePassword(enteredPass, storedHash);
    }

    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generatePassword(password: String): String? {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = getSalt()
        val spec = PBEKeySpec(chars, salt, iterations, 512)
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
