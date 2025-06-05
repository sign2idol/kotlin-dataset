package com.deathgun.dataset.storage

import io.github.cdimascio.dotenv.dotenv
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI

object R2StorageProvider : StorageProvider {

    private val dotenv = dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    private val accessKey = dotenv["CF_R2_ACCESS_KEY_ID"] ?: error("Missing CF_R2_ACCESS_KEY_ID in .env")
    private val secretKey = dotenv["CF_R2_SECRET_ACCESS_KEY"] ?: error("Missing CF_R2_SECRET_ACCESS_KEY in .env")
    private val accountId = dotenv["CF_R2_ACCOUNT_ID"] ?: error("Missing CF_R2_ACCOUNT_ID in .env")
    private val bucketName = dotenv["CF_R2_BUCKET_NAME"] ?: error("Missing CF_R2_BUCKET_NAME in .env")

    private val endpoint = URI.create("https://$accountId.r2.cloudflarestorage.com")

    private val client: S3Client by lazy {
        S3Client.builder()
            .region(Region.of("auto"))
            .endpointOverride(endpoint)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .chunkedEncodingEnabled(false)
                    .build()
            )
            .build()
    }

    override fun uploadFile(key: String, data: ByteArray, contentType: String) {
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        client.putObject(request, RequestBody.fromBytes(data))
    }

}