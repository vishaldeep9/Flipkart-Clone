package com.exm.flipkartclone.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.exm.flipkartclone.entity.AccessToken;
import com.exm.flipkartclone.entity.RefreshToken;
import com.exm.flipkartclone.repo.AccessTokenRepo;
import com.exm.flipkartclone.repo.RefreshTokenRepo;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class SchedularJobs {

	
	private AccessTokenRepo accessTokenRepo;

	private RefreshTokenRepo refreshTokenRepo;

	// Scheduled task to run every 6 hours
	@Scheduled(cron = "0 */6 * * * *") // Run every 6 hours
	public void cleanUpAllTheExpiredToken()
	{
		LocalDateTime now = LocalDateTime.now();
		List<AccessToken> accessTokenList = accessTokenRepo.findByExpirationBefore(now);
		accessTokenRepo.deleteAll(accessTokenList);
		
		List<RefreshToken> refreshTokenList = refreshTokenRepo.findByExpirationBefore(now);
		refreshTokenRepo.deleteAll(refreshTokenList);

	}
}
