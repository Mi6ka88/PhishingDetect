package crystalkate.phishingdetect

import crystalkate.phishingdetect.domain.ResultDetect
import crystalkate.phishingdetect.service.DetectPhishing
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class DetectPhishingTests {
    private val whoIsUrl = "https://www.reg.ru/whois/?dname="
    private val detectPhishing = DetectPhishing(
        whoIsUrl =whoIsUrl
    )

    @BeforeEach
    fun setUp() {
        mockkStatic(Jsoup::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Jsoup::class)
    }

    private val testURL = "https://www.bmw.ru/"
    private val expectedDomain = "bmw.ru"

    private val htmlContent = """
        <html>
            <body>
                <script>
                    eval("alert('test')");
                    window.location = "https://fake.com";
                    navigator.userAgent = "test";
                </script>
                <form method="post">
                    <input type="password">
                </form>
            </body>
        </html>
    """.trimIndent()

    @Test
    fun `should detect phishing parameters in html content`() {
        val mockDocument = mockk<Document>()
        every { mockDocument.html() } returns htmlContent

        val result = detectPhishing.detectPhishingParams(mockDocument)

        result.foundParams should containAll(
            "eval(",
            "window.location",
            "navigator.userAgent",
            "method=\"post\"",
            "input type=\"password\""
        )
        result.countCriticalParams shouldBe 7
        result.riskLevel shouldBe ResultDetect.RiskLevel.LOW
        result.isPhishing shouldBe false
    }

    @Test
    fun `extractDomain should return excepted domain`() {
        detectPhishing.extractDomain(testURL) shouldBe expectedDomain
    }

    @Test
    fun `checkOnWhoIs should return DomainWhoisInfo with correct data`() {
        val mockDocument = mockk<Document>()
        val mockStatusElement = mockk<org.jsoup.nodes.Element>()
        val expectedStatus = "домен занят"
        val expectedRegistrationDate = "1997-03-24T13:34:37Z"

        val mockConnection = mockk<Connection>()
        every { Jsoup.connect(whoIsUrl + expectedDomain) } returns mockConnection
        every { mockConnection.timeout(any()) } returns mockConnection
        every { mockConnection.followRedirects(any()) } returns mockConnection
        every { mockConnection.userAgent(any()) } returns mockConnection
        every { mockConnection.get() } returns mockDocument
        every { mockDocument.selectFirst("p.b-whois-domain-status__result") } returns mockStatusElement
        every { mockStatusElement.text() } returns expectedStatus
        every { mockDocument.selectFirst("p.p-whois__title-cell:contains(Дата регистрации)") } returns mockk {
            every { parent() } returns mockk {
                every { parent() } returns mockk {
                    every { nextElementSibling() } returns mockk {
                        every { selectFirst("p.p-whois__text-cell") } returns mockk {
                            every { text() } returns expectedRegistrationDate
                        }
                    }
                }
            }
        }

        val result = detectPhishing.checkOnWhoIs(expectedDomain)

        result.domain shouldBe expectedDomain
        result.status shouldBe expectedStatus
        result.registrationDate shouldBe expectedRegistrationDate
    }

    @Test
    fun `checkOnWhoIs should fallback to international domains when russian domain returns null`() {
        val mockDocument = mockk<Document>()
        val mockStatusElement = mockk<org.jsoup.nodes.Element>()
        val expectedStatus = "домен занят"
        val expectedRegistrationDate = "2020-01-15"

        val mockConnection = mockk<Connection>()
        every { Jsoup.connect(whoIsUrl + expectedDomain) } returns mockConnection
        every { mockConnection.timeout(any()) } returns mockConnection
        every { mockConnection.followRedirects(any()) } returns mockConnection
        every { mockConnection.userAgent(any()) } returns mockConnection
        every { mockConnection.get() } returns mockDocument
        every { mockDocument.selectFirst("p.b-whois-domain-status__result") } returns mockStatusElement
        every { mockStatusElement.text() } returns expectedStatus
        every { mockDocument.selectFirst("p.p-whois__title-cell:contains(Дата регистрации)") } returns null

        val mockTextCell = mockk<org.jsoup.nodes.Element>()
        val fullText = "Creation Date: 2020-01-15\nSome other text"

        every { mockDocument.selectFirst("p.p-whois__text-cell") } returns mockTextCell
        every { mockTextCell.text() } returns fullText

        val result = detectPhishing.checkOnWhoIs(expectedDomain)

        result.domain shouldBe expectedDomain
        result.status shouldBe expectedStatus
        result.registrationDate shouldBe expectedRegistrationDate
    }
}