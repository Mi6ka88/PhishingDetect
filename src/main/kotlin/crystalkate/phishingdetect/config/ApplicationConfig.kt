package crystalkate.phishingdetect.config

import crystalkate.phishingdetect.service.DetectPhishing
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
}