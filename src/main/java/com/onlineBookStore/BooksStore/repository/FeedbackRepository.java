package com.onlineBookStore.BooksStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlineBookStore.BooksStore.Entities.User;
import com.onlineBookStore.BooksStore.Entities.UserFeedback;

public interface FeedbackRepository extends JpaRepository<UserFeedback, Integer> {
	public UserFeedback findByFpuser(User u);
}
