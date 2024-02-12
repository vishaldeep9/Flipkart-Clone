package com.exm.flipkartclone.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.exm.flipkartclone.entity.User;

@Configuration
public class CacheBeanConfig {
	
	@Bean
	public CacheStore<User> userCacheStore(){
		
		return new CacheStore<>(Duration.ofMinutes(15));
	}

	
	@Bean
	public CacheStore<String> otpCacheStore(){
		
		return new CacheStore<String>(Duration.ofMinutes(15));
	}
}