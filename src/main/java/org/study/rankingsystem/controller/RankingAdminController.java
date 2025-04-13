package org.study.rankingsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.rankingsystem.service.RankingSyncService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/admin/ranking")
@RequiredArgsConstructor
@RestController
public class RankingAdminController {

	private final RankingSyncService rankingSyncService;

	@PostMapping("/sync")
	public ResponseEntity<String> syncRedisRanking() {
		rankingSyncService.syncTodayRankingFromDB();
		return ResponseEntity.ok("✅ ZSET 랭킹 동기화 완료");
	}

	@PostMapping("/profile-sync")
	public ResponseEntity<String> syncRedisProfiles() {
		rankingSyncService.syncUserProfilesToRedis();
		return ResponseEntity.ok("✅ HASH 프로필 동기화 완료");
	}
}
