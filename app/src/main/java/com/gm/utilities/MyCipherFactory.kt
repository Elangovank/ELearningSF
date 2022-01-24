package com.gm.utilities

import com.core.utils.AppPreferences
import fr.maxcom.http.CipherFactory
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MyCipherFactory() : CipherFactory {
    @Throws(GeneralSecurityException::class)
    override fun getCipher(): Cipher? {
        // you are free to choose your own Initialization Vector
        val initialIV = ByteArray(16)
        return rebaseCipher(initialIV)
    }

    @Throws(GeneralSecurityException::class)
    override fun rebaseCipher(iv: ByteArray): Cipher {
        // avoid the default security provider "AndroidOpenSSL" in Android 4.3+ (http://libeasy.alwaysdata.net/network/#provider)
        lateinit var c: Cipher
        try {
            c = Cipher.getInstance(GMKeys.VIDEO_ENCRYPTION_TRANSFORMATION, "BC")
        } catch (e: NoSuchAlgorithmException) {
            c = Cipher.getInstance(GMKeys.VIDEO_ENCRYPTION_TRANSFORMATION)
        }
        c.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(("1234567890123456").toByteArray(), "AES"),
            IvParameterSpec(iv)
        )
        return c
    }
}