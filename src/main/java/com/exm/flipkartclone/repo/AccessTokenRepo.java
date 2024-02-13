package com.exm.flipkartclone.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.AccessToken;
import com.exm.flipkartclone.entity.User;


public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);

	Optional<AccessToken> findByToken(String at);

	List<AccessToken> findByExpirationBefore(LocalDateTime now);

	Optional<User> findByUserAndIsBlocked(User user, boolean b);

	void save(User accessToken);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	

}
