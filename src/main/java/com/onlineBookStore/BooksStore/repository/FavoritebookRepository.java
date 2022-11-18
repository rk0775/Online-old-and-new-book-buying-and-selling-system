package com.onlineBookStore.BooksStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.FavoriteBooks;
import com.onlineBookStore.BooksStore.Entities.User;

public interface FavoritebookRepository extends JpaRepository<FavoriteBooks, Integer> {
	public FavoriteBooks findByFavoriteBookAndFavoriteUser(Book b, User u);
}
