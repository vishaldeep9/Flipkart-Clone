package com.exm.flipkartclone.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exm.flipkartclone.cache.CacheStore;
import com.exm.flipkartclone.entity.AccessToken;
import com.exm.flipkartclone.entity.Customer;
import com.exm.flipkartclone.entity.RefreshToken;
import com.exm.flipkartclone.entity.Seller;
import com.exm.flipkartclone.entity.User;
import com.exm.flipkartclone.exception.UserNotLoggedInException;
import com.exm.flipkartclone.repo.AccessTokenRepo;
import com.exm.flipkartclone.repo.CustomerRepo;
import com.exm.flipkartclone.repo.RefreshTokenRepo;
import com.exm.flipkartclone.repo.SellerRepo;
import com.exm.flipkartclone.repo.UserRepo;
import com.exm.flipkartclone.requestdto.AuthRequest;
import com.exm.flipkartclone.requestdto.OtpModel;
import com.exm.flipkartclone.requestdto.UserRequestDto;
import com.exm.flipkartclone.responcedto.AuthResponce;
import com.exm.flipkartclone.responcedto.UserResponceDto;
import com.exm.flipkartclone.security.JwtService;
import com.exm.flipkartclone.service.AuthService;
import com.exm.flipkartclone.util.CookieManager;
import com.exm.flipkartclone.util.MessageStructure;
import com.exm.flipkartclone.util.ResponceStructure;
import com.exm.flipkartclone.util.SimpleResponceStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private PasswordEncoder passwordEncoder;

	private JwtService jwtService;

	private AccessTokenRepo accessTokenRepo;

	private RefreshTokenRepo refreshTokenRepo;

	@Value("${myapp.access.expiry}")
	private int acccesExpiryInseconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInseconds;

	private CookieManager cookieManager;

	@Autowired
	private AuthenticationManager authenticationManager;

	private CacheStore<User> userCacheStore;

	private CacheStore<String> otpCacheStore;

	private ResponceStructure<UserResponceDto> responceStructure;

	private UserRepo userRepo;

	private CustomerRepo customerRepo;

	private SellerRepo sellerRepo;

	private JavaMailSender javaMailSender;

	private ResponceStructure<AuthResponce> authStructure;

	private SimpleResponceStructure<AuthResponce> authResponceStructure;

	

	public AuthServiceImpl(PasswordEncoder passwordEncoder, JwtService jwtService, AccessTokenRepo accessTokenRepo,
			RefreshTokenRepo refreshTokenRepo, CookieManager cookieManager, AuthenticationManager authenticationManager,
			CacheStore<User> userCacheStore, CacheStore<String> otpCacheStore,
			ResponceStructure<UserResponceDto> responceStructure, UserRepo userRepo, CustomerRepo customerRepo,
			SellerRepo sellerRepo, JavaMailSender javaMailSender, ResponceStructure<AuthResponce> authStructure,
			SimpleResponceStructure<AuthResponce> authResponceStructure) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
		this.cookieManager = cookieManager;
		this.authenticationManager = authenticationManager;
		this.userCacheStore = userCacheStore;
		this.otpCacheStore = otpCacheStore;
		this.responceStructure = responceStructure;
		this.userRepo = userRepo;
		this.customerRepo = customerRepo;
		this.sellerRepo = sellerRepo;
		this.javaMailSender = javaMailSender;
		this.authStructure = authStructure;
		this.authResponceStructure = authResponceStructure;
	}

	@Override
	public void revokeOther(String accessToken, String refreshToken) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		userRepo.findByUserName(username).ifPresent(user -> {

			blockAccessToken(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));

			blockRefreshToken(refreshTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));
		});

	}

	private void blockAccessToken(List<AccessToken> accessToken) {
		accessToken.forEach(at -> {
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});
	}

	private void blockRefreshToken(List<AccessToken> refreshToken) {
		refreshToken.forEach(rt -> {
			rt.setBlocked(true);
			refreshTokenRepo.save(rt);
		});
	}

	@Override
	public ResponseEntity<SimpleResponceStructure<AuthResponce>> revokeAll() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		userRepo.findByUserName(username).ifPresent(user -> {

			accessTokenRepo.findByUserAndIsBlocked(user, false).ifPresent(accessToken -> {
				accessToken.setBlocked(true);
				accessTokenRepo.save(accessToken);
			});

			refreshTokenRepo.findByUserAndIsBlocked(user, false).ifPresent(refreshToken -> {
				refreshToken.setBlocked(true);
				refreshTokenRepo.save(refreshToken);

			});

		});

		authResponceStructure.setMessage("revoked all devices succesfully");
		authResponceStructure.setStatus(HttpStatus.OK.value());

		return new ResponseEntity<SimpleResponceStructure<AuthResponce>>(authResponceStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponceStructure<AuthResponce>> logout(String refreshToken, String accessToken,
			HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		String rt = "";
		String at = "";

		if (accessToken == null && refreshToken == null)
			throw new UserNotLoggedInException("");

		accessTokenRepo.findByToken(at).ifPresent(accesToken -> {
			accesToken.setBlocked(true);
			accessTokenRepo.save(accesToken);
		});

		servletResponse.addCookie(cookieManager.invalidate(new Cookie(at, "")));
		servletResponse.addCookie(cookieManager.invalidate(new Cookie(rt, "")));
		authResponceStructure.setMessage("user has been logged out");
		authResponceStructure.setStatus(HttpStatus.GONE.value());
		return new ResponseEntity<SimpleResponceStructure<AuthResponce>>(authResponceStructure, HttpStatus.GONE);
	}

	@Override
	public ResponseEntity<ResponceStructure<AuthResponce>> login(AuthRequest authRequest,
			HttpServletResponse response) {
		String username = authRequest.getEmail().split("@")[0];
		// created token
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getPassword(),
				username);

		Authentication authenticate = authenticationManager.authenticate(token);
		if (!authenticate.isAuthenticated())
			throw new RuntimeException("Failed to Authenc=ticate the user");
		else {
			// generating the cookies and returning to the client
			return userRepo.findByUserName(username).map(user -> {
				grantAccess(response, user);

				return ResponseEntity.ok(authStructure.setStatus(HttpStatus.OK.value()).setMessage("")
						.setData(AuthResponce.builder().userId(user.getUserId()).userName(username)
								.role(user.getUserRole().name()).isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(acccesExpiryInseconds))
								.refreshExpriration(LocalDateTime.now().plusSeconds(refreshExpiryInseconds)).build()));

			}).get();
		}
	}

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
			log.error("This email Address doesnot exit " + OTP);
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

		if (otp == null)
			throw new RuntimeException("otp expired");
		if (user == null)
			throw new RuntimeException("Registration session expired");
		if (!otp.equals(otpModel.getOtp()))
			throw new RuntimeException("invalid otp");

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

	// for msking asycronous(simultenously perform each task) we used void ----and
	// donot use retur type
	@Async
	private void sendMail(MessageStructure messageStructure) throws MessagingException {
		MimeMessage createMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(createMimeMessage, true);
		helper.setTo(messageStructure.getTo());
		helper.setSubject(messageStructure.getSubject());
		helper.setSentDate(messageStructure.getSendDate());
		helper.setText(messageStructure.getText(), true); // true is used to enabled html document in the text
		javaMailSender.send(createMimeMessage);
	}

	private void sendOtpToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail()).subject("Complete your user registartion to Flipkart")
				.sendDate(new Date())
				.text("hey ," + user.getUserName() + "Good To see You registartion using otp" + "<h1>" + otp + "</h1>"
						+ "Note: the otp expires in 1 mintutes" + "<br></br>" + "with best regarding" + "Flipkart"

				).build());
	}

	private String generateOtp() {
		return String.valueOf(new Random().nextInt(100000, 999999));
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

	private void grantAccess(HttpServletResponse response, User user) {
		String accessToken = jwtService.generateAccessToken(user.getUserName());
		String refreshToken = jwtService.generateAccessToken(user.getUserName());

		// adding access and refresh tokens cookies to the response
		response.addCookie(cookieManager.configure(acccesExpiryInseconds, new Cookie("at", accessToken)));

		response.addCookie(cookieManager.configure(refreshExpiryInseconds, new Cookie("rt", refreshToken)));

		// saving the access and refresh cookie in to the database

		accessTokenRepo.save(AccessToken.builder().token(accessToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(acccesExpiryInseconds)).build());

		refreshTokenRepo.save(RefreshToken.builder().token(refreshToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInseconds)).build());

	}

}
