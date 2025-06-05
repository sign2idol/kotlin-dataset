package com.deathgun.dataset

import com.deathgun.dataset.storage.LocalStorageProvider
import com.deathgun.dataset.storage.R2StorageProvider
import com.deathgun.dataset.storage.StorageProvider
import io.github.cdimascio.dotenv.dotenv
import org.koin.core.module.Module
import org.koin.dsl.module

val storageModule: Module = module {
    single<StorageProvider> {
        val env = dotenv {
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
        when ((env["STORAGE_PROVIDER"] ?: "r2").lowercase()) {
            "local" -> LocalStorageProvider(java.io.File(env["LOCAL_STORAGE_PATH"] ?: "storage"))
            else -> R2StorageProvider
        }
    }
}
