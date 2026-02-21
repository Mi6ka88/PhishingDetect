# 🕵️ PhishingDetect

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-brightgreen.svg)
![React](https://img.shields.io/badge/React-18+-61DAFB?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5+-3178C6?logo=typescript&logoColor=white)
---

🔐 О проекте

**PhishingDetect** — это сервис для анализа веб-страниц и выявления признаков фишинга.

Система использует:

- 🧠 Анализ JavaScript-кода  
- 🌐 Проверку HTML-структуры  
- 📡 WHOIS-данные домена  
- 📊 Расчёт вероятности риска  

Проект помогает определить, является ли сайт потенциально вредоносным.

---

⚙️ Как это работает

1. Пользователь вводит URL
2. Сервис анализирует:
   - содержимое страницы
   - подозрительные параметры
   - возраст домена
3. Формируется:
   - уровень риска
   - процент вероятности
   - список найденных признаков
4. Возвращается структурированный JSON-ответ

---

🛠 Технологии

- **Backend:** Kotlin + Spring Boot
- **Анализ:** HTML / JavaScript parsing
- **Данные домена:** WHOIS
- **Frontend:** React
- **Архитектура:** REST API
