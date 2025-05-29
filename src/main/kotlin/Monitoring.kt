package com.deathgun.dataset

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.sentry.Sentry
import org.slf4j.event.*
import kotlin.text.toDouble

fun Application.configureMonitoring() {


    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    val dotenv = dotenv()

    val environment = dotenv["ENVIRONMENT"]

    Sentry.init { options ->
        options.dsn = dotenv["SENTRY_DSN"] ?: error("Missing SENTRY_DSN in .env")
        options.environment = environment // ou "development"
        options.release = "ktor-dataset@${dotenv["VERSION"]}" // utile pour versionner
        options.tracesSampleRate = dotenv["SENTRY_TRACES_SAMPLE_RATE"].toDouble() // pour activer la capture des performances (0.0 à 1.0)
        options.isDebug = environment === "development"
    }
}
