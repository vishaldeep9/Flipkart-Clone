package com.exm.flipkartclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

}
