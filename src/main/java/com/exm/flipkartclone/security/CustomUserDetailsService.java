package com.exm.flipkartclone.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.exm.flipkartclone.entity.User;
import com.exm.flipkartclone.repo.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return new CustomUserDetails(user);
	}

}