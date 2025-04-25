package org.study.rankingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.study.rankingsystem.domain.User;
import org.study.rankingsystem.infra.redis.dto.RedisUserProfile;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(String userId);

	@Query(value = "select u.userId, u.nickname, u.profileImageUrl, g.lastPlayedAt from User u join GameRecord g on u.userId = g.user.userId")
	List<RedisUserProfile> getRedisUserProfiles();
}