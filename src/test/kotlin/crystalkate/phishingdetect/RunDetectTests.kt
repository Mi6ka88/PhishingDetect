package crystalkate.phishingdetect

import crystalkate.phishingdetect.controller.DetectResponse
import crystalkate.phishingdetect.controller.RunDetect
import crystalkate.phishingdetect.domain.DomainWhoisInfo
import crystalkate.phishingdetect.domain.ResultDetect
import crystalkate.phishingdetect.service.DetectPhishing
import crystalkate.phishingdetect.service.JsoupWrapper
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jsoup.nodes.Document
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import kotlin.test.Test

class RunDetectTests {
    private val detectPhishing = mockk<DetectPhishing>()
    private val jsoupWrapper = mockk<JsoupWrapper>()
    private val controller = RunDetect(detectPhishing, jsoupWrapper)

    private val testResultDetect = ResultDetect(
        countCriticalParams = 9,
        foundParams = emptyList(),
        probability = 13.004926108374384,
        riskLevel = ResultDetect.RiskLevel.LOW,
        isPhishing = false,
        totalWeight = 13.2,
        maxPossibleWeight = 101.5,
        confidenceScore = 95.0
    )

    private val testResultDomainInfo = DomainWhoisInfo(
        domain = "bmw.ru",
        status = "занят",
        registrationDate = "24.03.1997"
    )
    private val testURL = "https://www.bmw.ru/"
    private val expectedDomain = "bmw.ru"



    @ParameterizedTest(name = "URL: {0} должен вернуть 400")
    @ValueSource(
        strings = [
            "htts://wikipedia.org",
            "www.wikipedia.org",
            "wikipedia.org",
            "",
            "ftp://wikipedia.org",
            "not-a-url",
            "http:/wikipedia.org",
            "https:/wikipedia.org"
        ]
    )
    fun `invalid urls should return 400`(invalidUrl: String) {
        val response = controller.processDetectPhishing(invalidUrl)

        HttpStatus.BAD_REQUEST shouldBe response.statusCode
        "Invalid URL $invalidUrl" shouldBe response.body
    }

    @Test
    fun `should return 200 OK with valid response when url is valid`() {
        val mockDocument = mockk<Document>()

        every { detectPhishing.extractDomain(testURL) } returns expectedDomain
        every { jsoupWrapper.fetchHtml(testURL) } returns mockDocument
        every { detectPhishing.detectPhishingParams(mockDocument) } returns testResultDetect
        every { detectPhishing.checkOnWhoIs(expectedDomain) } returns testResultDomainInfo

        val response = controller.processDetectPhishing(testURL)

        response.statusCode shouldBe HttpStatus.OK

        val responseBody = response.body as DetectResponse
        responseBody.detect shouldBe testResultDetect
        responseBody.whois shouldBe testResultDomainInfo

        verify(exactly = 1) { detectPhishing.extractDomain(testURL) }
        verify(exactly = 1) { jsoupWrapper.fetchHtml(testURL) }
        verify(exactly = 1) { detectPhishing.detectPhishingParams(mockDocument) }
        verify(exactly = 1) { detectPhishing.checkOnWhoIs(expectedDomain) }
    }
}