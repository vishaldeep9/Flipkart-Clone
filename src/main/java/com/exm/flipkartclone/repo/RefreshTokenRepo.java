package com.exm.flipkartclone.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.AccessToken;
import com.exm.flipkartclone.entity.RefreshToken;
import com.exm.flipkartclone.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>{

	List<RefreshToken> findByExpirationBefore(LocalDateTime now);

	Optional<RefreshToken> findByUserAndIsBlocked(User user, boolean b);

	void save(AccessToken rt);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	

}
