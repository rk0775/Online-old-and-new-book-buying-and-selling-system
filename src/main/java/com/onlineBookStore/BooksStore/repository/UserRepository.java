package com.onlineBookStore.BooksStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlineBookStore.BooksStore.Entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	public User findByUserEmail(String email);

	public User findByVerificationCode(String vcode);

	public User findByUserPassword(String code);

}
