package com.deathgun.dataset.storage

interface StorageProvider {
    fun uploadFile(key: String, data: ByteArray, contentType: String)
}
