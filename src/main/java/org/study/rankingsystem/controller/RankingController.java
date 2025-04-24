package org.study.rankingsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.study.rankingsystem.dto.AddScoreRequest;
import org.study.rankingsystem.dto.AddedScoredResponse;
import org.study.rankingsystem.dto.RankingTop10Response;
import org.study.rankingsystem.service.GameRecordService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RankingController {

	private final GameRecordService gameRecordService;

	@PostMapping("/game/scores")
	public ResponseEntity<AddedScoredResponse> addScore(@RequestBody AddScoreRequest request){
		AddedScoredResponse response = gameRecordService.addScore(request);
		return ResponseEntity.ok(response);
	}


	@GetMapping("/top10")
	public ResponseEntity<RankingTop10Response> getTop10(@RequestParam(defaultValue = "redis") String strategy) {
		return ResponseEntity.ok(gameRecordService.getTop10(strategy));
	}
}
