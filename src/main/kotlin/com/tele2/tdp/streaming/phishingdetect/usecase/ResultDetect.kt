package com.tele2.tdp.streaming.phishingdetect.usecase

data class ResultDetect(
    val countCriticalParams: Int,
    val foundParams: List<String>,
    val probability: Double,
    val isPhishing: Boolean,
) {
    companion object {
        private const val MAX_ACCEPT_COUNT = 8L

        fun createResultDetect(
            countCriticalParams: Int,
            foundParams: List<String>
        ): ResultDetect {
            val probabilityValue = (countCriticalParams.toDouble() / MAX_ACCEPT_COUNT * 100).coerceIn(0.0, 100.0)
            val isPhishingValue = probabilityValue > 50.0

            return ResultDetect(
                countCriticalParams = countCriticalParams,
                foundParams = foundParams,
                probability = probabilityValue,
                isPhishing = isPhishingValue
            )
        }
    }
}