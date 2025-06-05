package com.deathgun.dataset

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import com.deathgun.dataset.storageModule

fun main(args: Array<String>) {

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(storageModule)
    }
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
