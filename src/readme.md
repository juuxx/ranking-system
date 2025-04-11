# 📊 Ranking System - Spring Boot + Redis + MySQL

> 실시간 게임 랭킹 시스템 구현 프로젝트

이 프로젝트는 Redis ZSET을 활용하여 실시간 랭킹을 처리하고, MySQL에 사용자 및 게임 기록을 저장하는 토이 프로젝트입니다.
사용자와 게임 기록은 API로 처리되며, 랭킹은 Redis를 통해 빠르게 조회됩니다.

---

## ✅ 주요 기능

### 1. 사용자 등록
- 유저 ID, 닉네임, 프로필 이미지 등록
- 더미 유저 100명 자동 생성 (`DummyUserLoader`)

### 2. 게임 기록 저장 (API 예정)
- 게임 점수 기록 (개별 플레이 이력 `GameRecordLog`)
- 유저별 누적 점수, 마지막 플레이 시간 저장 (`GameRecord`)
- 플레이할 때마다 Redis ZSET (`ranking:game:{날짜}`)에 점수 반영

### 3. 랭킹 조회
- Redis ZSET에서 상위 N명 조회 (`ZREVRANGE`)
- 유저 프로필은 Redis Hash에서 TTL 관리
- Redis 장애 시 DB fallback 처리

### 4. 초기 더미 데이터
- `user1 ~ user100` 자동 생성
- 점수는 API를 통해 등록 (기록은 초기화되지 않음)

---

## ⚙️ 기술 스택

| 계층 | 기술 |
|------|------|
| 백엔드 | Spring Boot 3.x, JPA (Hibernate) |
| 데이터베이스 | MySQL 8 |
| 캐시 | Redis (ZSET + HASH) |
| 배포 도구 | Docker + Docker Compose |

---

## 🐳 Docker 실행 방법

```bash
docker compose up -d
```

- MySQL: `localhost:3306`
- Redis: `localhost:6379`



