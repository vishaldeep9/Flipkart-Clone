package com.exm.flipkartclone.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

//	Object findByUserName(String username);

	Optional<User> findByUserName(String string);

	

}
