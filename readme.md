# System Insight 스터디
## 📊 Ranking System - Spring Boot + Redis + MySQL

- **목표**: 사용자 점수 기반으로 실시간 게임 랭킹 제공하는 시스템 구현
- **기술 스택**: Spring Boot, JPA, Redis (ZSET, HASH), MySQL, Docker
- **환경**: 스터디 기반 개인/소규모 환경 중심 (하지만 운영 확장 고려함)


## 주요 도메인 모델

### 1. User (사용자 정보)

- userId, nickname, profileImageUrl, lastPlayed
- 연관관계:
    - `@OneToOne` → GameRecord (1:1, 최신 점수)
    - `@OneToMany` → GameRecordLog (1\:N, 모든 플레이 기록)

### 2. GameRecord (최신 누적 점수)

- user, totalScore, lastPlayedAt

### 3. GameRecordLog (플레이 로그)

- user, score, playedAt


## 기능 흐름: 점수 등록 API

### 📥 `POST /game/scores`

#### 요청 JSON

```json
{
  "nickname": "PlayerOne",
  "userId": "khs",
  "profileImageUrl": "https://cdn.example.com/images/player1.png",
  "score": 150
}
```

### 처리 단계

1. User DB 등록 or 갱신
2. GameRecordLog 저장 (이력)
3. GameRecord 누적 점수 반영 (없으면 생성)
4. Redis HASH 프로필 저장 (`user:profile:{userId}`)
5. Redis ZSET 점수 반영 (`ranking:game:{yyyyMMdd}`)

### TTL 전략

- HASH: 30분 (Top 10 조회 시 1시간 연장)
- ZSET: 1일 유지


## Top 10 랭킹 조회 API

### 📤 `GET /game-rankings/top10`

- ZSET에서 상위 10명 userId + score 조회
- 각 userId에 대해:
    - Redis HASH에서 프로필 조회
    - 없으면 DB fallback → Redis 저장
    - 있으면 TTL 1시간 연장
- 응답: rank, nickname, profileImageUrl, totalScore, lastPlayedAt


## 수동 동기화 API (스터디/테스트용)

### `POST /admin/ranking/sync`

- DB의 GameRecord 기준 → Redis ZSET 재집계

### `POST /admin/ranking/profile-sync`

- DB의 User 기준 → Redis HASH 프로필 일괄 저장

---

# 📊 성능 테스트 결과 요약 (nGrinder 기반)

## 테스트 대상 API

1. **POST /game/scores** : 점수 등록 (쓰기 부하 테스트)
2. **GET /top10** : 랭킹 조회 (읽기 부하 테스트)


## 테스트 환경
- 테스트 툴: nGrinder (Agent 1대)
- 서버: Spring Boot + Redis + MySQL (Docker 기반)
- Redis 구조: ZSET (점수), HASH (프로필 정보)
- 테스트 시간: 각 1분씩 진행


## POST /game/scores (쓰기 부하)

| VUser 수 | TPS | 최고 TPS | 평균 응답시간 | 에러 |
|----------|-----|----------|----------------|------|
| 99       | 332.4 | 484.0    | 292.4 ms       | 0    |
| 1,000    | 164.7 | 297.0    | 3,050.69 ms    | 0    |
| 3,000    | 50.1  | 194.5    | 9,059.43 ms    | 4,972 |

![Image](https://github.com/user-attachments/assets/c240684b-aad5-4b3f-a624-0c3a3b58e741)
![Image](https://github.com/user-attachments/assets/32cc6cdd-7f44-4b6a-86a7-09575c952ef4)
![Image](https://github.com/user-attachments/assets/56f0365e-293e-441c-8e70-cb0ca35b9e9e)
### 분석
- 3,000명 시 평균 응답 시간 9초 이상, TPS 급감, 에러 4,972건 발생
- 구조적으로 서버가 쓰기 요청을 감당하지 못하고 있음 (Timeout, Connection Refused 가능성)
- 병목 가능성: DB 커넥션 풀 고갈, Redis ZADD 연산 대기, GC Full Stop, Thread pool 포화

### 개선 방향
- 점수 등록을 비동기 큐(Kafka 등)로 분리하여 즉시 응답 구조로 전환
- Redis ZSET 연산을 배치 기반으로 전환 (집계 주기화)
- 서버 Thread pool, 커넥션 풀 최대치 상향 + 병렬성 조정
- Redis 클러스터 구성 또는 레디스 I/O 튜닝
- TPS 수집기 분산 처리 또는 에이전트 확장 적용 검토


## GET /top10 (읽기 부하)

| VUser 수 | TPS | 최고 TPS | 평균 응답시간 | 에러 |
|----------|-----|----------|----------------|------|
| 99       | 1,649.7 | 2,149.5 | 56.89 ms      | 0    |
| 1,000    | 456.5   | 824.5   | 1,029.84 ms   | 0    |
| 3,000    | 45.7    | 200.5   | 4,112.23 ms   | 560  |


![Image](https://github.com/user-attachments/assets/a2b697a0-ab78-473b-a975-4096e3d90abe)
![Image](https://github.com/user-attachments/assets/ac7ef8b2-fd81-4f9b-aeb0-79b691039cb4)
![Image](https://github.com/user-attachments/assets/0d9ee45d-9f0d-4992-b83e-da8187c775ca)
### 분석
- 99명 시 초당 1,600건 이상 처리 가능 (아주 우수)
- 1,000명 시 응답 시간 1초 초과 → UX 저하 우려
- 병목 가능성: Redis ZRANGE + HGET 조합, TTL 재설정, GC 지연

### 개선 방향
- 상위 10명 캐싱 전략 도입 (`TOP10:daily` 형태로 ZSET snapshot 유지)
- Redis TTL 설정 개선 (더 길게 유지하거나 Lazy reload)
- 서버 Thread pool/GC 튜닝, 요청 결과 CDN 캐시 고려
- Redis 클러스터화 및 수평 확장 고려


## 📌 결론
- **100명 수준의 요청에는 읽기/쓰기가 모두 안정적으로 동작**
- **1,000명 이상부터는 TPS 감소 및 응답 지연이 본격적으로 시작됨**
- **3,000명 이상부터는 에러 발생 → 구조적 병목의 실제 한계점**
- 구조 자체는 견고하나, 대규모 실서비스 전환 전 반드시 최적화 및 튜닝 필요
