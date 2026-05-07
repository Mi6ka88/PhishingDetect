package crystalkate.phishingdetect.usecase

data class ResultDetect(
    val countCriticalParams: Int,
    val foundParams: List<String>,
    val probability: Double,
    val riskLevel: RiskLevel,
    val isPhishing: Boolean,
    val totalWeight: Double = 0.0,
    val maxPossibleWeight: Double = 0.0,
    val confidenceScore: Double = 0.0
) {
    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    companion object {
        fun createResultDetect(
            countCriticalParams: Int,
            foundParams: List<String>,
            totalWeight: Double = 0.0,
            maxPossibleWeight: Double = 100.0,
            probability: Double = 0.0
        ): ResultDetect {

            val probabilityValue = if (probability != 0.0) {
                probability
            } else if (maxPossibleWeight > 0) {
                (totalWeight / maxPossibleWeight * 100).coerceIn(0.0, 100.0)
            } else {
                0.0
            }

            val riskLevel = when {
                probabilityValue < 25 -> RiskLevel.LOW
                probabilityValue < 50 -> RiskLevel.MEDIUM
                probabilityValue < 75 -> RiskLevel.HIGH
                else -> RiskLevel.CRITICAL
            }

            val isPhishingValue = when (riskLevel) {
                RiskLevel.MEDIUM -> true
                RiskLevel.HIGH -> true
                RiskLevel.CRITICAL -> true
                RiskLevel.LOW -> false
            }

            val confidenceScore = when {
                countCriticalParams == 0 -> 95.0
                countCriticalParams <= 3 -> 60.0
                countCriticalParams <= 6 -> 75.0
                else -> 95.0
            }

            return ResultDetect(
                countCriticalParams = countCriticalParams,
                foundParams = foundParams,
                probability = probabilityValue,
                riskLevel = riskLevel,
                isPhishing = isPhishingValue,
                totalWeight = totalWeight,
                maxPossibleWeight = maxPossibleWeight,
                confidenceScore = confidenceScore
            )
        }
    }
}
