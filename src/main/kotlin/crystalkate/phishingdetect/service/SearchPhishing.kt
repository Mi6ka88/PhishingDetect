package crystalkate.phishingdetect.service

import crystalkate.phishingdetect.domain.DomainWhoisInfo
import crystalkate.phishingdetect.domain.ResultDetect
import org.jsoup.nodes.Document

interface SearchPhishing {
    fun detectPhishingParams(doc: Document) : ResultDetect
    fun checkOnWhoIs(domain:String) : DomainWhoisInfo
}