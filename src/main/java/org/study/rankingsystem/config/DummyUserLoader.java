package org.study.rankingsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DummyUserLoader implements CommandLineRunner {
	private final UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {
		if (userRepository.count() > 0) return;

		for (int i = 1; i <= 100; i++) {
			String userId = "user" + i;
			User user = User.builder()
				.userId(userId)
				.nickname("Player" + i)
				.profileImageUrl("https://cdn.example.com/images/player" + i + ".png")
				.build();
			userRepository.save(user);
		}

		System.out.println("✅ 더미 유저 100명만 삽입 완료 (게임 기록 없음)");
	}
}
