package com.store.store9m.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.store9m.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	public UserDtls findByEmail(String email);

	public List<UserDtls> findByRole(String role);
	
}
