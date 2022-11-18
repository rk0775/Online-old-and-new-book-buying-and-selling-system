package com.onlineBookStore.BooksStore.Entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userId;

	@NotBlank(message = "user name not blank!!")
	@Size(max = 30, min = 4, message = "name must be greterthan 4 character or lessthan 30 character")
	private String userName;
	@Column(unique = true)
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Please Enter valid Email Address !!")
	private String userEmail;
	@NotBlank(message = "This field is required !!")
	private String userPassword;
	@NotBlank(message = "This field is required !!")
	@Pattern(message = "Please enter valid phone number", regexp = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$")
	private String userPhone;

	@NotBlank(message = "This field is required !!")
	@Pattern(regexp = "^[1-9][0-9]{5}$", message = "enter valid area pincode")
	private String userPincode;
	@NotBlank(message = "This field is required !!")
	private String userDistrict;
	@NotBlank(message = "address field not be a blank!!")
	@Size(max = 200, min = 5, message = "name must be greterthan 5 character or lessthan 30 character")
	private String userAdd;
	// @ColumnDefault(value = "default.png")
	private String userPic;
	@Column(length = 20)
	private String role;
	private String verificationCode;
	private boolean enabled;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "seller")
	private List<Book> books = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bookStore_id")
	private BookStore bookStore;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orderedUser")
	private List<BookOrder> UserPlaceOrders = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "favoriteUser")
	private List<FavoriteBooks> favoiteBooks = new ArrayList<>();

	public User(String userName, String userEmail, String userPassword, String userPhone, String userAddr,
			String userPic, List<Book> books) {
		super();
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userPhone = userPhone;
		this.userAddr = userAddr;
		this.userPic = userPic;
		this.books = books;

	}

	public User() {
	};// default constructor

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserAddr() {
		return userAddr;
	}

	public void setUserAddr(String userAddr) {
		this.userAddr = userAddr;
	}

	public String getUserPic() {
		return userPic;
	}

	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	/*
	 * public List<BookReview> getBookReviews() { return bookReviews; }
	 * 
	 * public void setBookReviews(List<BookReview> bookReviews) { this.bookReviews =
	 * bookReviews; }
	 */

	public BookStore getBookStore() {
		return bookStore;
	}

	public void setBookStore(BookStore bookStore) {
		this.bookStore = bookStore;
	}

	public String getUserPincode() {
		return userPincode;
	}

	public void setUserPincode(String userPincode) {
		this.userPincode = userPincode;
	}

	public String getUserDistrict() {
		return userDistrict;
	}

	public void setUserDistrict(String userDistrict) {
		this.userDistrict = userDistrict;
	}

	public List<BookOrder> getUserPlaceOrders() {
		return UserPlaceOrders;
	}

	public void setUserPlaceOrders(List<BookOrder> userPlaceOrders) {
		UserPlaceOrders = userPlaceOrders;
	}

	public List<FavoriteBooks> getFavoiteBooks() {
		return favoiteBooks;
	}

	public void setFavoiteBooks(List<FavoriteBooks> favoiteBooks) {
		this.favoiteBooks = favoiteBooks;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	

}
