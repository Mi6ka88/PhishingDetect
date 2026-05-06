package crystalkate.phishingdetect.usecase

import crystalkate.phishingdetect.usecase.DomainWhoisInfo.Companion.createDomainInfo
import crystalkate.phishingdetect.usecase.PhishingParams.Companion.allPhishingParams
import crystalkate.phishingdetect.usecase.ResultDetect.Companion.createResultDetect
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DetectPhishing(
    @Value("\${phishing.whois.url}")
    private val whoIsUrl: String
) : SearchPhishing {
    override fun detectPhishingParams(doc: Document): ResultDetect {
        var countAllParams = 0
        val parsePage = doc.html()
        val listPhishingParams = mutableListOf<String>()
        allPhishingParams.forEach { search ->
            if (parsePage.contains(search)) {
                listPhishingParams.add(search)
                countAllParams++
            }
        }
        return createResultDetect(
            countCriticalParams = countAllParams,
            foundParams = listPhishingParams,
            totalPossibleParams = allPhishingParams.size,
        )
    }

    override fun checkOnWhoIs(domain: String): DomainWhoisInfo {
        val content = Jsoup.connect(whoIsUrl + domain).get()
        val statusElement = content.selectFirst("p.b-whois-domain-status__result")
        val domainStatus = statusElement?.text() ?: "NotFound"
        val registrationDate = checkRegistrationDateOnRussianDomains(content)
            ?: checkRegistrationDateOnInternationalDomains(content)
        return createDomainInfo(
            domain = domain,
            status = domainStatus,
            registrationDate = registrationDate ?: "registrationDate not found"
        )
    }

    internal fun extractDomain(url: String): String {
        val regex = "(?:https?://)?(?:www\\.)?([^/]+)".toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: url
    }

    private fun checkRegistrationDateOnInternationalDomains(doc: Document): String? {
        val textCell = doc.selectFirst("p.p-whois__text-cell")
        val fullText = textCell?.text() ?: return null

        val pattern = Regex("Creation Date:\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{4})")
        val matchResult = pattern.find(fullText)

        return matchResult?.groupValues?.get(1)
    }

    private fun checkRegistrationDateOnRussianDomains(content: Element): String? {
        val registrationDate = content.selectFirst("p.p-whois__title-cell:contains(Дата регистрации)")
            ?.parent()
            ?.parent()
            ?.nextElementSibling()
            ?.selectFirst("p.p-whois__text-cell")
            ?.text()
            ?.trim()
        return registrationDate
    }
}
