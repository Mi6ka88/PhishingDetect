package crystalkate.phishingdetect.usecase

data class ResultDetect(
    val countCriticalParams: Int,
    val foundParams: List<String>,
    val probability: Double,
    val riskLevel: RiskLevel,
    val isPhishing: Boolean,
) {
    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    companion object {
        fun createResultDetect(
            countCriticalParams: Int,
            foundParams: List<String>,
            totalPossibleParams: Int
        ): ResultDetect {
            val probabilityValue = if (totalPossibleParams == 0) {
                0.0
            } else {
                (countCriticalParams.toDouble() / totalPossibleParams * 100)
                    .coerceIn(0.0, 100.0)
            }
            val riskLevel = when {
                probabilityValue < 30 -> RiskLevel.LOW
                probabilityValue < 60 -> RiskLevel.MEDIUM
                else -> RiskLevel.HIGH
            }

            val isPhishingValue = when(riskLevel) {
                RiskLevel.MEDIUM -> true
                RiskLevel.HIGH -> true
                RiskLevel.LOW -> false
            }

            return ResultDetect(
                countCriticalParams = countCriticalParams,
                foundParams = foundParams,
                probability = probabilityValue,
                riskLevel = riskLevel,
                isPhishing = isPhishingValue
            )
        }
    }
}