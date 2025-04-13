# System Insight 스터디
## 📊 Ranking System - Spring Boot + Redis + MySQL

- **목표**: 사용자 점수 기반으로 실시간 게임 랭킹 제공하는 시스템 구현
- **기술 스택**: Spring Boot, JPA, Redis (ZSET, HASH), MySQL, Docker
- **환경**: 스터디 기반 개인/소규모 환경 중심 (하지만 운영 확장 고려함)

---

## ✅ 주요 도메인 모델

### 1. User (사용자 정보)

- userId, nickname, profileImageUrl, lastPlayed
- 연관관계:
    - `@OneToOne` → GameRecord (1:1, 최신 점수)
    - `@OneToMany` → GameRecordLog (1\:N, 모든 플레이 기록)

### 2. GameRecord (최신 누적 점수)

- user, totalScore, lastPlayedAt

### 3. GameRecordLog (플레이 로그)

- user, score, playedAt

---

## ⚙️ 기능 흐름: 점수 등록 API

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

---

## 📊 Top 10 랭킹 조회 API

### 📤 `GET /game-rankings/top10`

- ZSET에서 상위 10명 userId + score 조회
- 각 userId에 대해:
    - Redis HASH에서 프로필 조회
    - 없으면 DB fallback → Redis 저장
    - 있으면 TTL 1시간 연장
- 응답: rank, nickname, profileImageUrl, totalScore, lastPlayedAt

---

## 🔁 수동 동기화 API (스터디/테스트용)

### `POST /admin/ranking/sync`

- DB의 GameRecord 기준 → Redis ZSET 재집계

### `POST /admin/ranking/profile-sync`

- DB의 User 기준 → Redis HASH 프로필 일괄 저장
