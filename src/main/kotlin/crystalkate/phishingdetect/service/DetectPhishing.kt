package crystalkate.phishingdetect.service

import crystalkate.phishingdetect.domain.DomainWhoisInfo
import crystalkate.phishingdetect.domain.DomainWhoisInfo.Companion.createDomainInfo
import crystalkate.phishingdetect.domain.ResultDetect
import crystalkate.phishingdetect.domain.PhishingParams.Companion.allPhishingParams
import crystalkate.phishingdetect.domain.ResultDetect.Companion.createResultDetect
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class DetectPhishing(
    private val whoIsUrl: String
) : SearchPhishing {
    override fun detectPhishingParams(doc: Document): ResultDetect {
        val htmlContent = doc.html()
        val foundParams = mutableListOf<String>()
        var totalWeight = 0.0
        val maxPossibleWeight = allPhishingParams.sumOf { it.weight }

        allPhishingParams.forEach { param ->
            val isFound = if (param.regex != null) {
                param.regex.containsMatchIn(htmlContent)
            } else {
                htmlContent.contains(param.pattern, ignoreCase = true)
            }
            if (isFound) {
                foundParams.add(param.pattern)
                totalWeight += param.weight
            }
        }

        return createResultDetect(
            countCriticalParams = foundParams.size,
            foundParams = foundParams,
            totalWeight = totalWeight,
            maxPossibleWeight = maxPossibleWeight
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

        val pattern = Regex("Creation Date:\\s*(\\d{4}-\\d{2}-\\d{2})")
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
