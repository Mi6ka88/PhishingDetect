package com.tele2.tdp.streaming.phishingdetect.controller

import com.tele2.tdp.streaming.phishingdetect.usecase.DetectPhishing
import com.tele2.tdp.streaming.phishingdetect.usecase.DomainWhoisInfo
import com.tele2.tdp.streaming.phishingdetect.usecase.ResultDetect
import org.jsoup.Jsoup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RunDetect(
    private val detectPhishing: DetectPhishing,
) {
    @GetMapping("/runDetect")
    private fun processDetectPhishing(@RequestParam url:String ): ResponseEntity<Any> {
        val domain = extractDomain(url)
        val connect = Jsoup.connect(url)
            .timeout(10000)
            .userAgent("Mozilla/5.0")
            .get()
        val resultDetect = detectPhishing.detectPhishingParams(connect)
        val resultDomainStatus = detectPhishing.checkOnWhoIs(domain)

        return ResponseEntity.ok(DetectResponse(resultDetect, resultDomainStatus))
    }

    private fun extractDomain(url: String): String {
        val regex = "(?:https?://)?(?:www\\.)?([^/]+)".toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: url
    }
}

data class DetectResponse(
    val detect: ResultDetect,
    val whois: DomainWhoisInfo
)