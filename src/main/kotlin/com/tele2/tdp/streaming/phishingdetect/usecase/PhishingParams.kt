package com.tele2.tdp.streaming.phishingdetect.usecase

class PhishingParams {

    companion object {
        private val obfuscationPatterns = listOf(
            "eval(",
            "atob(",
            "unescape(",
            "String.fromCharCode(",
            "setTimeout(",
            "setInterval("
        )

        private val redirectPatterns = listOf(
            "window.location",
            "location.href",
            "document.location",
            "location.replace(",
            "meta http-equiv=\"refresh\""
        )

        private val fingerprintingPatterns = listOf(
            "navigator.userAgent",
            "navigator.platform",
            "navigator.language",
            "navigator.webdriver",
            "screen.width",
            "screen.height",
            "document.cookie",
            "localStorage",
            "sessionStorage"
        )

        private val formPatterns = listOf(
            "input type=\"password\"",
            "input type=\"email\"",
            "method=\"post\"",
            "autocomplete=\"off\""
        )

        private val hiddenElementPatterns = listOf(
            "iframe",
            "display:none",
            "visibility:hidden",
            "opacity:0"
        )

        internal val allPhishingParams: List<String> =
            obfuscationPatterns +
            redirectPatterns +
            fingerprintingPatterns +
            formPatterns +
            hiddenElementPatterns
    }
}
