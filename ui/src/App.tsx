import React, { useState, ChangeEvent, useEffect } from "react";
import "./App.css";

interface ResultDetect {
  countCriticalParams: number;
  foundParams: string[];
  probability: number;
  riskLevel: "LOW" | "MEDIUM" | "HIGH";
  isPhishing: boolean;
}

interface DomainWhoisInfo {
  domain: string;
  status: string;
  registrationDate: string;
}

interface DetectResponse {
  detect: ResultDetect;
  whois: DomainWhoisInfo;
}

function App() {
  const [url, setUrl] = useState("");
  const [data, setData] = useState<DetectResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isDark, setIsDark] = useState(true);

  useEffect(() => {
    document.body.className = isDark ? "" : "light-theme";
  }, [isDark]);

  const handleCheck = async () => {
    if (!url) return;

    setLoading(true);
    setError(null);
    setData(null);

    try {
      const response = await fetch(
          `http://localhost:9090/runDetect?url=${encodeURIComponent(url)}`
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Не удалось проверить сайт");
      }

      const result: DetectResponse = await response.json();
      setData(result);
    } catch (err) {
      setError(
          err instanceof Error ? err.message : "Не удалось проверить сайт"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && !loading) {
      handleCheck();
    }
  };

  const getRiskClass = (risk: string) => {
    switch (risk) {
      case "LOW":
        return "risk-low";
      case "MEDIUM":
        return "risk-medium";
      case "HIGH":
        return "risk-high";
      default:
        return "";
    }
  };

  return (
      <div className="container">
        <div className="theme-toggle" onClick={() => setIsDark(!isDark)}>
          {isDark ? "🌙" : "☀️"}
        </div>

        <h1>🛡 Phishing Detector</h1>

        <div className="input-block">
          <input
              className="url-input"
              type="text"
              placeholder="Введите URL сайта..."
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              onKeyPress={handleKeyPress}
          />
          <button onClick={handleCheck} disabled={loading}>
            {loading ? "Проверка..." : "Проверить"}
          </button>
        </div>

        {loading && (
            <div className="progress-container">
              <div className="hourglass-wrapper">
                <svg
                    className="hourglass"
                    viewBox="0 0 100 140"
                    xmlns="http://www.w3.org/2000/svg"
                >
                  {/* Верхняя колба (перевернутый треугольник с закруглениями) */}
                  <path
                      d="M32,10 Q32,25 50,42 Q68,25 68,10"
                      fill="rgba(59, 130, 246, 0.15)"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                  />

                  {/* Нижняя колба */}
                  <path
                      d="M32,130 Q32,115 50,98 Q68,115 68,130"
                      fill="rgba(59, 130, 246, 0.15)"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                  />

                  {/* Горловина (узкая часть посередине) */}
                  <rect
                      x="44"
                      y="58"
                      width="12"
                      height="24"
                      fill="rgba(59, 130, 246, 0.2)"
                      stroke="currentColor"
                      strokeWidth="1.5"
                  />

                  {/* Декоративная линия на горловине */}
                  <line
                      x1="42"
                      y1="68"
                      x2="58"
                      y2="68"
                      stroke="currentColor"
                      strokeWidth="1"
                      strokeOpacity="0.5"
                  />

                  <g className="sand-top">
                    {/* Песок над горловиной */}
                    <path
                        d="M44,54 Q47,48 50,54 Q53,48 56,54"
                        fill="currentColor"
                        opacity="0.8"
                    />
                    <circle cx="48" cy="50" r="1.5" fill="currentColor" opacity="0.7"/>
                    <circle cx="52" cy="50" r="1.5" fill="currentColor" opacity="0.7"/>
                    <circle cx="50" cy="46" r="1.2" fill="currentColor" opacity="0.6"/>
                    <circle cx="47" cy="44" r="1" fill="currentColor" opacity="0.5"/>
                    <circle cx="53" cy="44" r="1" fill="currentColor" opacity="0.5"/>
                  </g>

                  <g className="sand-middle">
                    <line
                        x1="50"
                        y1="56"
                        x2="50"
                        y2="80"
                        stroke="currentColor"
                        strokeWidth="1.5"
                        strokeDasharray="3 2"
                        opacity="0.8"
                    />
                    <circle cx="50" cy="62" r="1" fill="currentColor" opacity="0.9"/>
                    <circle cx="50" cy="66" r="0.8" fill="currentColor" opacity="0.8"/>
                    <circle cx="50" cy="70" r="0.7" fill="currentColor" opacity="0.7"/>
                    <circle cx="50" cy="74" r="0.6" fill="currentColor" opacity="0.6"/>
                  </g>

                  <g className="sand-bottom">
                    <ellipse
                        cx="50"
                        cy="124"
                        rx="16"
                        ry="8"
                        fill="currentColor"
                        opacity="0.3"
                    />
                    <path
                        d="M36,124 Q43,110 50,118 Q57,110 64,124"
                        fill="currentColor"
                        opacity="0.4"
                    />
                    <circle cx="44" cy="120" r="2" fill="currentColor" opacity="0.7"/>
                    <circle cx="50" cy="118" r="2.5" fill="currentColor" opacity="0.8"/>
                    <circle cx="56" cy="120" r="2" fill="currentColor" opacity="0.7"/>
                    <circle cx="47" cy="122" r="1.8" fill="currentColor" opacity="0.6"/>
                    <circle cx="53" cy="122" r="1.8" fill="currentColor" opacity="0.6"/>
                    <circle cx="50" cy="125" r="2" fill="currentColor" opacity="0.5"/>
                  </g>

                  <rect
                      x="28"
                      y="4"
                      width="44"
                      height="132"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2.5"
                      rx="8"
                      strokeOpacity="0.8"
                  />

                  <rect
                      x="26"
                      y="2"
                      width="48"
                      height="5"
                      rx="2.5"
                      fill="currentColor"
                      fillOpacity="0.6"
                  />

                  <rect
                      x="26"
                      y="133"
                      width="48"
                      height="5"
                      rx="2.5"
                      fill="currentColor"
                      fillOpacity="0.6"
                  />

                  <line
                      x1="34"
                      y1="7"
                      x2="34"
                      y2="133"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeOpacity="0.4"
                  />
                  <line
                      x1="66"
                      y1="7"
                      x2="66"
                      y2="133"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeOpacity="0.4"
                  />
                </svg>
              </div>
              <p className="loading-text">Проверка сайта...</p>
            </div>
        )}

        {error && <p className="error">❌ {error}</p>}

        {data && (
            <div className="result fade-in">
              <div className={`risk ${getRiskClass(data.detect.riskLevel)}`}>
                <h2>Уровень риска: {data.detect.riskLevel}</h2>
                <p>Вероятность: {data.detect.probability.toFixed(1)}%</p>
                <p>
                  {data.detect.isPhishing
                      ? "⚠️ Фишинговый сайт"
                      : "✅ Сайт безопасен"}
                </p>
              </div>

              {(data.detect.riskLevel === "MEDIUM" || data.detect.riskLevel === "HIGH") && (
                  <div className="card">
                    <h3>🔍 Найденные признаки ({data.detect.foundParams.length})</h3>
                    {data.detect.foundParams.length === 0 ? (
                        <p>Подозрительных признаков не обнаружено</p>
                    ) : (
                        <ul>
                          {data.detect.foundParams.map((param, index) => (
                              <li key={index}>{param}</li>
                          ))}
                        </ul>
                    )}
                  </div>
              )}

              <div className="card">
                <h3>🌐 WHOIS</h3>
                <p>
                  <strong>Домен:</strong> {data.whois.domain}
                </p>
                <p>
                  <strong>Статус:</strong> {data.whois.status}
                </p>
                <p>
                  <strong>Дата регистрации:</strong>{" "}
                  {data.whois.registrationDate
                      ? new Date(data.whois.registrationDate).toLocaleDateString(
                          "ru-RU"
                      )
                      : "Не определена"}
                </p>
              </div>
            </div>
        )}
      </div>
  );
}

export default App;