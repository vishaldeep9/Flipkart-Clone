package com.exm.flipkartclone.entity;

import com.exm.flipkartclone.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)  //it will create different/separate table for seller or customer ,,to avoid null data
@Table(name = "users")
public class User {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String userName;
	
	@Column(unique = true)
	private String email;
	private String password;

	private UserRole userRole;
	
	private boolean isEmailVerified;
	private boolean isDeleated;
	
}
