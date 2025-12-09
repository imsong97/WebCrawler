package com.ch0pp4.data

interface WebCrawlerRepository {
    suspend fun sendSlackMessageCoroutine(text: String): Boolean
}