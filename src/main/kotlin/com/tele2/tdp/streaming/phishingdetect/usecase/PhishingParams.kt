package com.tele2.tdp.streaming.phishingdetect.usecase

class PhishingParams {
    companion object {
        private val geolocationParams: List<String> = listOf(
            "api.ipify.org",
            "ipapi.co",
            "ipwho.is",
            "getGeolocation",
            "location.country",
            "location.city",
            "location.latitude",
            "location.longitude"
        )

        private val keystrokeParams: List<String> = listOf(
            "keystrokes.push",
            "key: e.key",
            "keyup"
        )

        private val browserInfoParams: List<String> = listOf(
            "deviceMemory",
            "hardwareConcurrency",
            "screenResolution"
        )

        private val captchaParams: List<String> = listOf(
            "generateCaptcha",
            "currentCaptcha",
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        )

        private val successPageParams: List<String> = listOf(
            "showSuccessPage",
            "success-page",
            "Вход выполнен успешно",
            "Проверка учетных данных завершена"
        )

        private val redirectParams: List<String> = listOf(
            "manual-redirect",
            "window.location.href"
        )


        private val stealthParams: List<String> = listOf(
            ".catch(() => {})",
        )


        private val fakeElements: List<String> = listOf(
            "success-page",
            "manual-redirect",
            "redirectProgress",
            "security-notice"
        )

        private val suspiciousForms: List<String> = listOf(
            "form:not([action])",
            "form[action^=http]",
            "form[action*=localhost]"
        )

        private val dataCollection: List<String> = listOf(
            "keystrokes",
            "browserInfo"
        )

        internal val allPhishingParams: List<String> =
            geolocationParams +
            keystrokeParams +
            browserInfoParams +
            captchaParams +
            successPageParams +
            redirectParams +
            stealthParams +
            fakeElements +
            suspiciousForms +
            dataCollection
    }
}