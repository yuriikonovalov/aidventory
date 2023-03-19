package com.aidventory.core.backup.hash

internal interface BackupHashManager {
    /**
     * Generates a hash for the provided [body] input.
     *
     * @param body [String] used for generating a hash
     * @return [String] of the generated hash value
     */
    fun getHash(body: String): String

    /**
     * Checks if the provided [hash] is equal to a hash that is generated for
     * the provided [body] value.
     */
    fun isHashEqual(body: String, hash: String): Boolean
}