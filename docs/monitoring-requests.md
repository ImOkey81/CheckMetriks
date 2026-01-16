# Примеры запросов для мониторинга и результаты проверок

Ниже приведены примеры HTTP-запросов к сервису мониторинга, который сохраняет результаты проверок и предоставляет их через API.
Примеры подойдут как для `curl`, так и для Postman (метод, URL и параметры одинаковые).

## Базовые эндпоинты мониторинга

> Базовый URL сервиса: `http://localhost:8080`

## Как добавить хост/микросервис в проверку

Сервис мониторинга принимает цели через POST-запрос и начинает опрашивать их автоматически с интервалом `monitoring.poll-interval-ms`.

**Запрос**
```http
POST /api/targets
```

**Пример (HTTP-проверка микросервиса):**
```bash
curl -X POST "http://localhost:8080/api/targets" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "billing-service",
    "name": "Billing Service",
    "checks": [
      {
        "type": "HTTP",
        "url": "http://billing.internal:8080/actuator/health",
        "expectStatus": 200,
        "maxLatencyMs": 800,
        "timeoutMs": 3000
      }
    ]
  }'
```

**Пример (TCP-проверка хоста/порта):**
```bash
curl -X POST "http://localhost:8080/api/targets" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "postgresql-tcp",
    "name": "PostgreSQL",
    "checks": [
      {
        "type": "TCP",
        "host": "db.internal",
        "port": 5432,
        "connectTimeoutMs": 500
      }
    ]
  }'
```

### 1) Получить последние результаты проверок по цели

**Запрос**
```http
GET /api/targets/{targetId}/last?limit=10
```

**Пример (curl)**
```bash
curl "http://localhost:8080/api/targets/auth-service/last?limit=5"
```

**Пример ответа**
```json
[
  {
    "id": 101,
    "targetId": "auth-service",
    "checkType": "HTTP",
    "ok": true,
    "latencyMs": 142,
    "message": "HTTP 200",
    "createdAt": "2024-04-18T10:22:31.123Z"
  },
  {
    "id": 100,
    "targetId": "auth-service",
    "checkType": "HTTP",
    "ok": false,
    "latencyMs": 3001,
    "message": "Timeout after 3000ms",
    "createdAt": "2024-04-18T10:22:16.120Z"
  }
]
```

### 2) Получить последние неуспешные проверки по цели

**Запрос**
```http
GET /api/targets/{targetId}/failures?limit=50
```

**Пример (curl)**
```bash
curl "http://localhost:8080/api/targets/redis-tcp/failures?limit=10"
```

**Пример ответа**
```json
[
  {
    "id": 88,
    "targetId": "redis-tcp",
    "checkType": "TCP",
    "ok": false,
    "latencyMs": 501,
    "message": "Connection refused",
    "createdAt": "2024-04-18T09:58:11.004Z"
  }
]
```

## Примеры запросов для Postman

### Коллекция запросов (шаблон)

1. **POST** `{{baseUrl}}/api/targets`
   - Body (raw JSON):
     ```json
     {
       "id": "billing-service",
       "name": "Billing Service",
       "checks": [
         {
           "type": "HTTP",
           "url": "http://billing.internal:8080/actuator/health",
           "expectStatus": 200,
           "maxLatencyMs": 800,
           "timeoutMs": 3000
         }
       ]
     }
     ```
2. **GET** `{{baseUrl}}/api/targets/{{targetId}}/last`
   - Query Params:
     - `limit`: `10`
3. **GET** `{{baseUrl}}/api/targets/{{targetId}}/failures`
   - Query Params:
     - `limit`: `50`

**Переменные окружения в Postman**
```text
baseUrl = http://localhost:8080
targetId = auth-service
```

## Полезные служебные эндпоинты (Spring Actuator)

Если нужно проверять сам сервис мониторинга, доступны стандартные Actuator эндпоинты:

```text
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
GET /actuator/prometheus
```
