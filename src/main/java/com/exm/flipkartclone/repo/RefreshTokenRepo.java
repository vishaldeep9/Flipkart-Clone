package com.exm.flipkartclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>{

}
