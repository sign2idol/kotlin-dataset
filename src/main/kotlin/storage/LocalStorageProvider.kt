package com.deathgun.dataset.storage

import java.io.File

class LocalStorageProvider(private val baseDir: File) : StorageProvider {
    override fun uploadFile(key: String, data: ByteArray, contentType: String) {
        val file = File(baseDir, key)
        file.parentFile?.mkdirs()
        file.writeBytes(data)
    }
}
