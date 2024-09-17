package com.store.store9m.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.store.store9m.model.UserDtls;

public interface UserService {

	public UserDtls saveUser(UserDtls user);

	public UserDtls getUserByEmail(String email);
	
	public List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	public void increaseFailedAttempt(UserDtls user);

	public void userAccountLock(UserDtls user);

	public boolean unlockAccountTimeExpired(UserDtls user);

	public UserDtls updateUserProfile(UserDtls user,MultipartFile img);
	
	
}
