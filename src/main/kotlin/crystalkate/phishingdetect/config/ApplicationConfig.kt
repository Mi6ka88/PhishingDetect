package crystalkate.phishingdetect.config

import crystalkate.phishingdetect.service.DetectPhishing
import crystalkate.phishingdetect.service.JsoupWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {
    @Bean
    fun detectPhishingDetect(
        @Value("\${phishing.whois.url}")
        whoisUrl: String
    ): DetectPhishing {
        return DetectPhishing(
            whoIsUrl = whoisUrl
        )
    }

    @Bean
    fun jsoupWrapper(
        @Value("\${phishing.whois.userAgent}")
        userAgent: String,
        @Value("\${phishing.whois.timeout}")
        timeout: Int
    ): JsoupWrapper {
        return JsoupWrapper(
            userAgent = userAgent,
            timeout = timeout
        )
    }
}