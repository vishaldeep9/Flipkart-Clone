package com.exm.flipkartclone.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exm.flipkartclone.entity.Seller;

public interface SellerRepo  extends JpaRepository<Seller, Integer>{

}
