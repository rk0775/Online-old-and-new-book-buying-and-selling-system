package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.BookRating;
import com.onlineBookStore.BooksStore.Entities.User;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {
	public BookRating findByRateuserAndBook(User u, Book b);

	public List<BookRating> findByBook(Book n);
}
