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
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    document.body.className = isDark ? "" : "light-theme";
  }, [isDark]);

  useEffect(() => {
    let interval: NodeJS.Timeout;

    if (loading) {
      setProgress(0);
      interval = setInterval(() => {
        setProgress((prev) => (prev >= 95 ? prev : prev + 1));
      }, 15);
    } else {
      setProgress(100);
    }

    return () => clearInterval(interval);
  }, [loading]);

  const handleCheck = async () => {
    if (!url) return;

    setLoading(true);
    setError(null);
    setData(null);

    try {
      const response = await fetch(
          `http://localhost:9090/runDetect?url=${encodeURIComponent(url)}`
      );

      if (!response.ok) throw new Error();

      const result: DetectResponse = await response.json();
      setData(result);
    } catch {
      setError("Не удалось проверить сайт");
    } finally {
      setLoading(false);
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

  const getProgressColor = () => {
    if (progress < 40) return "#22c55e";
    if (progress < 70) return "#facc15";
    return "#ef4444";
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
          />
          <button onClick={handleCheck} disabled={loading}>
            {loading ? "Проверка..." : "Проверить"}
          </button>
        </div>

        {loading && (
            <div className="progress-container">
              <div className="progress-circle">
                <svg>
                  <circle className="bg" cx="60" cy="60" r="54" />
                  <circle
                      className="progress"
                      cx="60"
                      cy="60"
                      r="54"
                      style={{
                        stroke: getProgressColor(),
                        strokeDashoffset: 339 - (339 * progress) / 100,
                      }}
                  />
                </svg>
                <div className="progress-text">{progress}%</div>
              </div>
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

              <div className="card">
                <h3>🌐 WHOIS</h3>
                <p><strong>Домен:</strong> {data.whois.domain}</p>
                <p><strong>Статус:</strong> {data.whois.status}</p>
                <p>
                  <strong>Дата регистрации:</strong>{" "}
                  {data.whois.registrationDate
                      ? new Date(data.whois.registrationDate).toLocaleDateString("ru-RU")
                      : "Не определена"}
                </p>
              </div>
            </div>
        )}
      </div>
  );
}

export default App;