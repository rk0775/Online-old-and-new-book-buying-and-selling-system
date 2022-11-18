package com.onlineBookStore.BooksStore.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BookCategorys {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int categoryId;
	private String categoryName;

	@ManyToOne
	private Book bookCategory;

	public BookCategorys() {
		// TODO Auto-generated constructor stub
	}

	public BookCategorys(int categoryId, String categoryName, Book bookCategory) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.bookCategory = bookCategory;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Book getBookCategory() {
		return bookCategory;
	}

	public void setBookCategory(Book bookCategory) {
		this.bookCategory = bookCategory;
	}

}
