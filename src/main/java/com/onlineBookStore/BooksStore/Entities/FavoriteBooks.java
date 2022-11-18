package com.onlineBookStore.BooksStore.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FavoriteBooks {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int favoriteBookId;

	@ManyToOne

	private Book favoriteBook;

	@ManyToOne
	private User favoriteUser;

	public int getFavoriteBookId() {
		return favoriteBookId;
	}

	public void setFavoriteBookId(int favoriteBookId) {
		this.favoriteBookId = favoriteBookId;
	}

	public Book getFavoriteBook() {
		return favoriteBook;
	}

	public void setFavoriteBook(Book favoriteBook) {
		this.favoriteBook = favoriteBook;
	}

	public User getFavoriteUser() {
		return favoriteUser;
	}

	public void setFavoriteUser(User favoriteUser) {
		this.favoriteUser = favoriteUser;
	}
}
