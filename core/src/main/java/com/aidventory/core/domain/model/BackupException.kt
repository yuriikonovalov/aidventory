package com.aidventory.core.domain.model

sealed class BackupException : Throwable()
object HashNotEqualException : BackupException()
object VersionNotEqualException : BackupException()