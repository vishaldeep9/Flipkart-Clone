package com.exm.flipkartclone.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exm.flipkartclone.entity.Customer;
import com.exm.flipkartclone.entity.Seller;
import com.exm.flipkartclone.entity.User;
import com.exm.flipkartclone.exception.UniqueConstraintViolationException;
import com.exm.flipkartclone.repo.CustomerRepo;
import com.exm.flipkartclone.repo.SellerRepo;
import com.exm.flipkartclone.repo.UserRepo;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.ResponceStructure;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private ResponceStructure<UserResponceDto> responceStructure;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private SellerRepo sellerRepo;

	// <T extends Users>----> DataType of T, T--> Actual Data returning
	@SuppressWarnings("unchecked")
	public <T extends User> T mapToUser(UserRequestDto userRequestDto) {
		User users = null;
		switch (userRequestDto.getUserRole()) {

		case CUSTOMER -> {
			users = new Customer();
		}
		case SELLER -> {
			users = new Seller();
		}
		default -> throw new RuntimeException();
		}
		users.setEmail(userRequestDto.getEmail());
		users.setPassword(userRequestDto.getPassword());
		users.setUserRole(userRequestDto.getUserRole());
		users.setUserName(users.getEmail().split("@")[0]);
		users.setDeleated(false);
		users.setEmailVerified(false);

		return (T) users;
	}

	public UserResponceDto mapToUserResponce(User users) {
		return UserResponceDto.builder().email(users.getEmail()).userId(users.getUserId()).userRole(users.getUserRole())
				.userName(users.getUserName()).isDeleated(users.isDeleated()).isEmailVerified(users.isEmailVerified()).build();
	}
	

	private User saveUser(UserRequestDto userRequest) {

		User user = null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER -> {
			user = customerRepo.save(mapToUser(userRequest));
		}
		case SELLER -> {
			user = sellerRepo.save(mapToUser(userRequest));
		}
		default -> throw new RuntimeException();
		}
		return user;
	}

	@Override
	public ResponseEntity<ResponceStructure<UserResponceDto>> registerUser(UserRequestDto userRequest) {
		
        User user= userRepo.findByUserName(userRequest.getEmail().split("@")[0]).map(u ->{
			
				if(u.isEmailVerified()) throw  new UniqueConstraintViolationException("user already exits with this specified email Id",HttpStatus.BAD_REQUEST,
						"this email id is already present present in database");
				else {
					//send an email to the client with otp
				}
			return u;
			
		}).orElse(saveUser(userRequest));
//		if (userRepo.existsByEmail(userRequest.getEmail()))throw new RuntimeException();	
//		User user2=mapToUser(userRequest);
//		user2=saveUser(user2);
//		UserResponceDto userResponceDto=mapToUserResponce(user2);
//		if (!userRepo.exitsByEmail(userRequest.getEmail())) {
//			User user = mapToUser(userRequest);
//			user.setUserName(user.getEmail().split("@")[0]);
//			userRepo.save(user);
//
//			if (user instanceof Customer) {
//				Customer customer = (Customer) user;
//				customerRepo.save(customer);
//			} else {
//				Seller seller = (Seller) user;
//				sellerRepo.save(seller);
//			}
//		}
		return new ResponseEntity<ResponceStructure<UserResponceDto>>(responceStructure
				.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("Please Verify your mail Id Using OTP sent ")
				.setData(mapToUserResponce(user)),HttpStatus.ACCEPTED);
	}

}
