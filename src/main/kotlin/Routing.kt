package com.deathgun.dataset

import com.deathgun.dataset.storage.S3ClientProvider
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import java.net.URLConnection
import java.util.UUID

fun Application.configureRouting() {
    routing {
        post("/upload") {
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val filename = part.originalFileName?.generateUniqueFileName()

                    val contentType = URLConnection
                        .guessContentTypeFromName(filename)
                        ?: "application/octet-stream"

                    val bytes = part.provider().readRemaining().readByteArray()

                    val key = "uploads/$filename"
                    S3ClientProvider.uploadFile(key, bytes, contentType)
                }
                part.dispose()
            }
            call.respond(status = HttpStatusCode.NoContent, message = "")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
    }
}

fun String.generateUniqueFileName(): String {
    val extension = this
        .substringAfterLast('.', "")
        .takeIf { it.isNotBlank() }
        ?.lowercase()

    val uuid = UUID.randomUUID().toString()

    return if (extension != null) "$uuid.$extension" else uuid
}