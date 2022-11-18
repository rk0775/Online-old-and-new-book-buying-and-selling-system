package com.onlineBookStore.BooksStore.Entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BookRating {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int ratingId;

	private int bookRate;

	@ManyToOne
	private User rateuser;
	@Column(length = 1000)
	private String review;
	private LocalDate date;
	@ManyToOne
	private Book book;

	public int getRatingId() {
		return ratingId;
	}

	public void setRatingId(int ratingId) {
		this.ratingId = ratingId;
	}

	public int getBookRate() {
		return bookRate;
	}

	public void setBookRate(int bookRate) {
		this.bookRate = bookRate;
	}

	public User getRateuser() {
		return rateuser;
	}

	public void setRateuser(User rateuser) {
		this.rateuser = rateuser;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book bookrating) {
		this.book = bookrating;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
