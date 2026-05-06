package crystalkate.phishingdetect

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PhishingDetectApplication

fun main(args: Array<String>) {
    runApplication<PhishingDetectApplication>(*args)
}
