package com.tele2.tdp.streaming.phishingdetect.usecase

import com.tele2.tdp.streaming.phishingdetect.usecase.DomainWhoisInfo.Companion.createDomainInfo
import com.tele2.tdp.streaming.phishingdetect.usecase.PhishingParams.Companion.allPhishingParams
import com.tele2.tdp.streaming.phishingdetect.usecase.ResultDetect.Companion.createResultDetect
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component

@Component
class DetectPhishing : SearchPhishing {
    private val whoIsUrl: String = "https://www.reg.ru/whois/?dname="
    override fun detectPhishingParams(doc: Document):ResultDetect {
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
            foundParams = listPhishingParams
        )
    }

    override fun checkOnWhoIs(domain: String): DomainWhoisInfo {
        val content = Jsoup.connect(whoIsUrl+domain).get()
        val statusElement = content.selectFirst("p.b-whois-domain-status__result")
        val domainStatus = statusElement?.text() ?: "NotFound"
        val registrationDate = content.selectFirst("p.p-whois__title-cell:contains(Дата регистрации)")
            ?.parent()
            ?.parent()
            ?.nextElementSibling()
            ?.selectFirst("p.p-whois__text-cell")
            .toString()
            .substring(30,50)
        return createDomainInfo(
            domain = domain,
            status = domainStatus,
            registrationDate = registrationDate
        )
    }
}
