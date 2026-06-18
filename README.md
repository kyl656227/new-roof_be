# new-roof-be

민방위 대피소 위치 기반 조회 서비스의 백엔드 API 서버입니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- Spring WebFlux (WebClient)
- H2 (개발) / MySQL (운영)

## 시작하기

### 사전 준비

[공공데이터포털](https://www.data.go.kr)에서 **행정안전부_민방위대피소** API 서비스키를 발급받습니다.

### 환경 설정

`src/main/resources/application-local.yml` 파일을 생성하고 발급받은 키를 입력합니다. (git에 올라가지 않습니다)

```yaml
shelter:
  api:
    key: 발급받은_서비스키
```

또는 환경변수로 전달할 수 있습니다.

```bash
export SHELTER_API_KEY=발급받은_서비스키
```

### 실행

```bash
./gradlew bootRun
```

서버가 `http://localhost:8080` 에서 실행됩니다.

## 데이터 로드 방식

앱 시작 시 다음 순서로 대피소 데이터를 DB에 적재합니다.

1. 공공데이터 API 호출 (전국 데이터, 페이지네이션)
2. API 실패 시 → 내장 CSV 파일 폴백 (`shelter.csv`)

이후 모든 조회는 DB에서 처리합니다.

## API

### 내 주변 대피소 목록

```
GET /api/shelters/nearby
```

| 파라미터 | 타입 | 기본값 | 설명 |
|---|---|---|---|
| `lat` | double | 필수 | 위도 |
| `lng` | double | 필수 | 경도 |
| `radiusM` | double | 10000 | 검색 반경 (미터) |

**응답 예시**

```json
{
  "success": true,
  "message": "success",
  "data": [
    {
      "shelterId": "S201000005",
      "name": "면목마젤란21아파트(지하주차장 1층)",
      "address": "서울특별시 중랑구 겸재로30길 42",
      "lat": 37.587,
      "lng": 127.084,
      "capacity": 4958,
      "shelterType": "지상",
      "managerName": "서울특별시 중랑구청",
      "managerPhone": "02-2094-1601",
      "distanceM": 9635.6
    }
  ]
}
```

### 대피소 상세

```
GET /api/shelters/{shelterId}
```

## H2 콘솔 (개발 환경)

`http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:newroof`
- Username: `sa`
- Password: (없음)
