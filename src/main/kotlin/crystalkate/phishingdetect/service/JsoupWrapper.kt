package crystalkate.phishingdetect.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupWrapper (
    private val userAgent: String,
    private val timeout: Int
): HtmlFetcher {

    override fun fetchHtml(url: String): Document {
        return Jsoup.connect(url)
            .timeout(timeout)
            .followRedirects(false)
            .userAgent(userAgent)
            .get()
    }
}