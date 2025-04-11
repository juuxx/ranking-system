package org.study.rankingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.study.rankingsystem.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findTop10ByOrderByUserIdAsc();

	Optional<User> findByUserId(String userId);
}