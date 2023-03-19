package com.aidventory.core.backup.hash

import com.google.android.gms.common.util.Hex
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Implements [BackupHashManager] and uses SHA-256 algorithm to create and check a hash.
 */
internal class SHA256BackupHashManager @Inject constructor() : BackupHashManager {
    private val messageDigest = MessageDigest.getInstance(HASH_ALGORITHM)

    override fun getHash(body: String): String {
        messageDigest.update(SALT.toByteArray())
        messageDigest.update(body.toByteArray())
        val bytes = messageDigest.digest()
        return Hex.bytesToStringLowercase(bytes)
    }

    override fun isHashEqual(body: String, hash: String): Boolean {
        return hash == getHash(body)
    }

    companion object {
        private const val HASH_ALGORITHM = "SHA-256"
        private const val SALT = "dkvmwe2weks41ed"
    }
}