package crystalkate.phishingdetect.domain

data class PhishingParameter(
    val pattern: String,
    val weight: Double,
    val category: String,
    val regex: Regex? = null,
    val description: String = ""
)

class PhishingParams {

    companion object {
        private val obfuscationPatterns = listOf(
            PhishingParameter("eval(", 2.0, "obfuscation", Regex("eval\\s*\\("), "Динамическое исполнение кода"),
            PhishingParameter("Function(", 2.0, "obfuscation", Regex("Function\\s*\\("), "Создание функции из строки"),
            PhishingParameter("atob(", 1.8, "obfuscation", Regex("atob\\s*\\("), "Декодирование Base64"),
            PhishingParameter("btoa(", 1.8, "obfuscation", Regex("btoa\\s*\\("), "Кодирование Base64"),
            PhishingParameter("String.fromCharCode(", 1.8, "obfuscation", Regex("String\\.fromCharCode\\s*\\("), "Создание строк из кодов"),
            PhishingParameter("unescape(", 1.7, "obfuscation", Regex("unescape\\s*\\("), "Декодирование строк"),
            PhishingParameter("replace(/\\\\u/", 1.6, "obfuscation", Regex("replace\\s*\\(/\\\\\\\\u"), "Unicode обфускация"),
            PhishingParameter("charCodeAt", 1.5, "obfuscation", Regex("charCodeAt\\s*\\("), "Работа с кодами символов"),
            PhishingParameter("\\\\x[0-9a-fA-F]{2}", 1.6, "obfuscation", Regex("\\\\\\\\x[0-9a-fA-F]{2}"), "Hex-кодирование"),
            PhishingParameter("\\\\u[0-9a-fA-F]{4}", 1.6, "obfuscation", Regex("\\\\\\\\u[0-9a-fA-F]{4}"), "Unicode кодирование")
        )

        private val redirectPatterns = listOf(
            PhishingParameter("window.location", 1.9, "redirect", Regex("window\\.location"), "Переадресация страницы"),
            PhishingParameter("location.href", 1.9, "redirect", Regex("location\\.href"), "Переадресация по ссылке"),
            PhishingParameter("location.replace(", 1.9, "redirect", Regex("location\\.replace\\s*\\("), "Замена страницы в истории"),
            PhishingParameter("document.location", 1.8, "redirect", Regex("document\\.location"), "Переадресация через document"),
            PhishingParameter("meta http-equiv=\"refresh\"", 1.7, "redirect", Regex("meta.*http-equiv\\s*=\\s*['\"]refresh['\"]"), "Meta редирект"),
            PhishingParameter("<a href=\"javascript:", 1.8, "redirect", Regex("<a\\s+href\\s*=\\s*['\"]javascript:"), "JavaScript в href"),
            PhishingParameter("onload=", 1.6, "redirect", Regex("onload\\s*="), "Обработчик onload"),
            PhishingParameter("top.location", 1.7, "redirect", Regex("top\\.location"), "Перенаправление родительского фрейма")
        )

        private val fingerprintingPatterns = listOf(
            PhishingParameter("navigator.userAgent", 1.5, "fingerprinting", Regex("navigator\\.userAgent"), "Получение User-Agent"),
            PhishingParameter("navigator.platform", 1.4, "fingerprinting", Regex("navigator\\.platform"), "Определение платформы"),
            PhishingParameter("navigator.language", 1.3, "fingerprinting", Regex("navigator\\.language"), "Получение языка"),
            PhishingParameter("navigator.webdriver", 1.6, "fingerprinting", Regex("navigator\\.webdriver"), "Обнаружение автоматизации"),
            PhishingParameter("screen.width", 1.2, "fingerprinting", Regex("screen\\.width"), "Получение ширины экрана"),
            PhishingParameter("screen.height", 1.2, "fingerprinting", Regex("screen\\.height"), "Получение высоты экрана"),
            PhishingParameter("document.cookie", 1.8, "fingerprinting", Regex("document\\.cookie"), "Работа с cookies"),
            PhishingParameter("localStorage", 1.8, "fingerprinting", Regex("localStorage"), "Работа с localStorage"),
            PhishingParameter("sessionStorage", 1.7, "fingerprinting", Regex("sessionStorage"), "Работа с sessionStorage"),
            PhishingParameter("localStorage.setItem", 1.9, "fingerprinting", Regex("localStorage\\.setItem"), "Сохранение данных в браузер"),
            PhishingParameter("JSON.stringify", 1.4, "fingerprinting", Regex("JSON\\.stringify"), "Сериализация данных"),
            PhishingParameter("XMLHttpRequest", 1.5, "fingerprinting", Regex("XMLHttpRequest"), "HTTP запросы в фоне")
        )

        private val formPatterns = listOf(
            PhishingParameter("input type=\"password\"", 1.8, "form", Regex("input[^>]*type\\s*=\\s*['\"]password['\"]"), "Поле пароля"),
            PhishingParameter("input type=\"email\"", 1.5, "form", Regex("input[^>]*type\\s*=\\s*['\"]email['\"]"), "Поле email"),
            PhishingParameter("input type=\"hidden\"", 1.6, "form", Regex("input[^>]*type\\s*=\\s*['\"]hidden['\"]"), "Скрытое поле"),
            PhishingParameter("method=\"post\"", 1.3, "form", Regex("method\\s*=\\s*['\"]post['\"]"), "POST запрос"),
            PhishingParameter("autocomplete=\"off\"", 1.7, "form", Regex("autocomplete\\s*=\\s*['\"]off['\"]"), "Отключение автозаполнения"),
            PhishingParameter("action=\"\"", 1.8, "form", Regex("action\\s*=\\s*['\"]['\"]"), "Форма без action или на себя"),
            PhishingParameter("form.*submit", 1.4, "form", Regex("form[^>]*onsubmit"), "Обработчик отправки формы"),
            PhishingParameter("<form", 1.0, "form", Regex("<form"), "Наличие формы (не опасно само по себе)")
        )

        private val hiddenElementPatterns = listOf(
            PhishingParameter("iframe", 1.4, "hidden", Regex("<iframe"), "Встроенный фрейм (может быть legitim)"),
            PhishingParameter("display:none", 1.6, "hidden", Regex("display\\s*:\\s*none"), "Скрытый элемент CSS"),
            PhishingParameter("visibility:hidden", 1.6, "hidden", Regex("visibility\\s*:\\s*hidden"), "Невидимый элемент"),
            PhishingParameter("opacity:0", 1.5, "hidden", Regex("opacity\\s*:\\s*0"), "Полностью прозрачный элемент"),
            PhishingParameter("width:0", 1.7, "hidden", Regex("width\\s*:\\s*0"), "Элемент нулевой ширины"),
            PhishingParameter("height:0", 1.7, "hidden", Regex("height\\s*:\\s*0"), "Элемент нулевой высоты"),
            PhishingParameter("position:absolute", 1.3, "hidden", Regex("position\\s*:\\s*absolute"), "Абсолютное позиционирование")
        )

        private val externalResourcePatterns = listOf(
            PhishingParameter("fetch(", 1.5, "external", Regex("fetch\\s*\\("), "Fetch API запрос"),
            PhishingParameter("XMLHttpRequest", 1.5, "external", Regex("XMLHttpRequest"), "XHR запрос"),
            PhishingParameter("<script src=", 1.3, "external", Regex("<script[^>]+src"), "Внешний скрипт"),
            PhishingParameter("<img src=", 1.2, "external", Regex("<img[^>]+src"), "Внешнее изображение"),
            PhishingParameter("navigator.sendBeacon", 1.7, "external", Regex("navigator\\.sendBeacon"), "Отправка данных маяка"),
            PhishingParameter("<link rel=\"stylesheet\"", 1.2, "external", Regex("<link[^>]+rel\\s*=\\s*['\"]stylesheet['\"]"), "Внешняя таблица стилей")
        )

        private val suspiciousPatterns = listOf(
            PhishingParameter("alert(", 1.3, "suspicious", Regex("alert\\s*\\("), "Всплывающее окно"),
            PhishingParameter("confirm(", 1.4, "suspicious", Regex("confirm\\s*\\("), "Диалог подтверждения"),
            PhishingParameter("prompt(", 1.5, "suspicious", Regex("prompt\\s*\\("), "Запрос ввода"),
            PhishingParameter("setInterval", 1.4, "suspicious", Regex("setInterval\\s*\\("), "Периодическое выполнение"),
            PhishingParameter("setTimeout", 1.4, "suspicious", Regex("setTimeout\\s*\\("), "Отложенное выполнение"),
            PhishingParameter("Object.defineProperty", 1.6, "suspicious", Regex("Object\\.defineProperty"), "Переопределение свойств"),
            PhishingParameter("console.log", 1.0, "suspicious", Regex("console\\.log"), "Логирование (низкий риск)"),
            PhishingParameter("debugger", 1.5, "suspicious", Regex("debugger"), "Точка отладки"),
            PhishingParameter("\"use strict\"", 0.8, "suspicious", Regex("\"use strict\""), "Strict mode (легитимно)")
        )

        private val eventPatterns = listOf(
            PhishingParameter("onmousedown", 1.3, "event", Regex("onmousedown\\s*="), "Обработчик mousedown"),
            PhishingParameter("onmouseover", 1.2, "event", Regex("onmouseover\\s*="), "Обработчик mouseover"),
            PhishingParameter("oncontextmenu", 1.4, "event", Regex("oncontextmenu\\s*="), "Отключение контекстного меню"),
            PhishingParameter("oncopy", 1.5, "event", Regex("oncopy\\s*="), "Обработчик копирования"),
            PhishingParameter("onpaste", 1.5, "event", Regex("onpaste\\s*="), "Обработчик вставки"),
            PhishingParameter("onkeypress", 1.4, "event", Regex("onkeypress\\s*="), "Обработчик нажатия клавиши")
        )

        internal val allPhishingParams: List<PhishingParameter> =
            obfuscationPatterns +
                    redirectPatterns +
                    fingerprintingPatterns +
                    formPatterns +
                    hiddenElementPatterns +
                    externalResourcePatterns +
                    suspiciousPatterns +
                    eventPatterns

        internal val allPhishingParamsStrings: List<String> = allPhishingParams.map { it.pattern }
    }
}
