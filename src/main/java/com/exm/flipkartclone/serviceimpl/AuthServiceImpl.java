package com.exm.flipkartclone.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exm.flipkartclone.cache.CacheStore;
import com.exm.flipkartclone.entity.Customer;
import com.exm.flipkartclone.entity.Seller;
import com.exm.flipkartclone.entity.User;
import com.exm.flipkartclone.repo.CustomerRepo;
import com.exm.flipkartclone.repo.SellerRepo;
import com.exm.flipkartclone.repo.UserRepo;
import com.exm.flipkartclone.requestdto.OtpModel;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.MessageStructure;
import com.exm.flipkartclone.util.ResponceStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	PasswordEncoder passwordEncoder;

	private CacheStore<User> userCacheStore;

	private CacheStore<String> otpCacheStore;

	private ResponceStructure<UserResponceDto> responceStructure;

	private UserRepo userRepo;

	private CustomerRepo customerRepo;

	private SellerRepo sellerRepo;

	private JavaMailSender javaMailSender;
	
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
		users.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
		users.setUserRole(userRequestDto.getUserRole());
		users.setUserName(users.getEmail().split("@")[0]);
		users.setDeleated(false);
		users.setEmailVerified(false);

		return (T) users;
	}

	public UserResponceDto mapToUserResponce(User users) {
		return UserResponceDto.builder().email(users.getEmail()).userId(users.getUserId()).userRole(users.getUserRole())
				.userName(users.getUserName()).isDeleated(users.isDeleated()).isEmailVerified(users.isEmailVerified())
				.build();
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

		if (userRepo.existsByEmail(userRequest.getEmail()))
			throw new RuntimeException("user already exits with this specified email Id");
		String OTP = generateOtp();
		User user = mapToUser(userRequest);
		userCacheStore.add(userRequest.getEmail(), user);
		otpCacheStore.add(userRequest.getEmail(), OTP);
 
		
		
			try {
				sendOtpToMail(user, OTP);
			} catch (MessagingException e) {
				log.error("This email Address doesnot exit "+ OTP);
				e.printStackTrace();
			}
		
//        User user= userRepo.findByUserName(userRequest.getEmail().split("@")[0]).map(u ->{
//			
//				if(u.isEmailVerified()) throw  new UniqueConstraintViolationException("user already exits with this specified email Id",HttpStatus.BAD_REQUEST,
//						"this email id is already present present in database");
//				else {
//					//send an email to the client with otp
//				}
//			return u;
//			
//		}).orElse(saveUser(userRequest));
//		if (userRepo.existsByEmail(userRequest.getEmail()))throw new RuntimeException("user already exits with this specified email Id");	
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
		return new ResponseEntity<ResponceStructure<UserResponceDto>>(
				responceStructure.setStatus(HttpStatus.ACCEPTED.value()).setMessage("Please Verify through Opt " + OTP)
						.setData(mapToUserResponce(user)),
				HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<String> verifyOtp(OtpModel otpModel) {

		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());

		if (otp == null) throw new RuntimeException("otp expired");
		if (user == null) throw new RuntimeException("Registration session expired");
		if (!otp.equals(otpModel.getOtp())) throw new RuntimeException("invalid otp");

		user.setEmailVerified(true);
		userRepo.save(user);
		return new ResponseEntity<String>("Registration Succesfull", HttpStatus.CREATED);
//		String exOTP=otpCacheStore.get("key");
//		//validating for null
//		if(exOTP!=null)  {
//			
//			//validating for correctness
//			if(exOTP.equals(otpModel)) 
//			return new ResponseEntity<String>(exOTP,HttpStatus.OK);
//			else return new ResponseEntity<String>("otp is invalid",HttpStatus.OK);
//		}
//		return new ResponseEntity<String>("otp is expired",HttpStatus.OK);
	}

	//for msking asycronous(simultenously perform each task) we used void ----and donot use retur type
	@Async
	private void sendMail(MessageStructure messageStructure) throws MessagingException {
		MimeMessage createMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(createMimeMessage, true);
		helper.setTo(messageStructure.getTo());
		helper.setSubject(messageStructure.getSubject());
		helper.setSentDate(messageStructure.getSentDate());
		helper.setText(messageStructure.getText(),true); //true is used to enabled html document in the text
		javaMailSender.send(createMimeMessage);
	}
	
	private void sendOtpToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
		.to(user.getEmail())
		.subject("Complete your user registartion to Flipkart")
		.sentDate(new Date())
		.text(
				"hey ,"+user.getUserName()
				+"Good To see You registartion using otp"
				+"<h1>"+otp+"</h1>"
				+"Note: the otp expires in 1 mintutes"
				+"<br></br>"
				+"with best regarding"
				+"Flipkart"
				
				).build());
	}
	
	private String generateOtp() {
		return String.valueOf(new Random().nextInt(100000, 999999));
	}

}
