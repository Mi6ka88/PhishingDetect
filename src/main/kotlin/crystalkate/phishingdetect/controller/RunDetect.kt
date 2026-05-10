package crystalkate.phishingdetect.controller

import crystalkate.phishingdetect.service.DetectPhishing
import crystalkate.phishingdetect.domain.DomainWhoisInfo
import crystalkate.phishingdetect.domain.ResultDetect
import crystalkate.phishingdetect.service.JsoupWrapper
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class RunDetect(
    private val detectPhishing: DetectPhishing,
    private val jsoupWrapper: JsoupWrapper
)   {
    @GetMapping("/runDetect")
    fun processDetectPhishing(@RequestParam url:String ): ResponseEntity<Any> {
        val isValidUrl = isUrlValid(url)
        if (!isValidUrl) return ResponseEntity.badRequest().body("Invalid URL $url")
        val domain = detectPhishing.extractDomain(url)
        val connect = jsoupWrapper.fetchHtml(url)
        val resultDetect = detectPhishing.detectPhishingParams(connect)
        val resultDomainStatus = detectPhishing.checkOnWhoIs(domain)

        return ResponseEntity.ok(DetectResponse(resultDetect, resultDomainStatus))
    }
}

data class DetectResponse(
    val detect: ResultDetect,
    val whois: DomainWhoisInfo
)

private fun isUrlValid(url: String): Boolean {
    val validator = UrlValidator(arrayOf("http", "https"))
    return validator.isValid(url)
}