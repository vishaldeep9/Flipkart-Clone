package com.exm.flipkartclone.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.AccessToken;


public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);

	Optional<AccessToken> findByToken(String at);

}
