package crystalkate.phishingdetect.usecase

import org.jsoup.nodes.Document

interface SearchPhishing {
    fun detectPhishingParams(doc: Document) : ResultDetect
    fun checkOnWhoIs(domain:String) : DomainWhoisInfo
}