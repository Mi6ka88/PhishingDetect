package crystalkate.phishingdetect.domain

data class DomainWhoisInfo(
    val domain: String,
    val status: String,
    val registrationDate: String
){
    companion object{
        fun createDomainInfo(
            domain: String,
            status: String,
            registrationDate: String
        ): DomainWhoisInfo {
            val domainStatus = when(status) {
                "занят" -> "домен $status"
                "свободен" -> "домен $status"
                else -> status
            }
            return DomainWhoisInfo(
                domain = domain,
                status = domainStatus,
                registrationDate = registrationDate
            )
        }
    }
}