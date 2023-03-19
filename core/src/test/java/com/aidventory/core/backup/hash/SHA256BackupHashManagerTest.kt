package com.aidventory.core.backup.hash

import com.google.common.truth.Truth.*
import org.junit.Test

class SHA256BackupHashManagerTest {
    private val hash = "c0f3e8139e423ce0a10b2b772451f717f9498edfdd72e4e3f98a059d5769bf84"
    private val body = "test body"
    private val manager = SHA256BackupHashManager()

    @Test
    fun getHash_shouldReturnExpectedHash() {
        val actualHash = manager.getHash(body)
        assertThat(actualHash).isEqualTo(hash)
    }

    @Test
    fun getHash_shouldReturnChangedHash() {
        val actualHash = manager.getHash(body + "changed")
        assertThat(actualHash).isNotEqualTo(hash)
    }

    @Test
    fun isHashEqual_shouldReturnTrue() {
        val actual = manager.isHashEqual(body, hash)
        assertThat(actual).isTrue()
    }


    @Test
    fun isHashEqual_whenBodyChanged_shouldReturnFalse() {
        val actual = manager.isHashEqual(body + "a", hash)
        assertThat(actual).isFalse()
    }


    @Test
    fun isHashEqual_whenHashChanged_shouldReturnFalse() {
        val actual = manager.isHashEqual(body, hash.replaceFirst("e", "b"))
        assertThat(actual).isFalse()
    }
}