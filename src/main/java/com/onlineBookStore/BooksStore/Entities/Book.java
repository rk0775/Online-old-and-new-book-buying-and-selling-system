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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int bookId;
	@NotBlank(message = "Book title is required..")
	@Size(min = 3, max = 40, message = "book title must be greter than 3 letters")
	@Column(name = "bookTitle")
	private String bookTitle;
	// @Pattern(regexp = "^[1-9]\\d{0,7}(?:\\.\\d{1,4})?|\\.\\d{1,4}$", message =
	// "please enter the valid book price")
	private int bookPrice;
	private int storeId;
	/*
	 * @NotBlank(message = "Book ISBN number is needed...")
	 * 
	 * @org.hibernate.validator.constraints.ISBN
	 * 
	 * @Column(unique = true) private String ISBN;
	 */
	@NotBlank(message = "Book Auther name is required..")
	@Size(min = 3, max = 40, message = "book title must be greter than 3 letters")
	private String bookAuthor;
	@NotBlank(message = "Book Discription is nessesary..")
	@Size(min = 10, message = "book title must be greter than 10 letters")
	@Column(length = 2000)
	private String bookDescription;
	// @ColumnDefault(value = "default.png")
	private String bookCover;
	private boolean sale;
	private boolean isBestOfYear;
	private String bookLanguage;
	private String bookType;// old new

	/*
	 * @Override public String toString() { return "Book [bookId=" + bookId +
	 * ", bookTitle=" + bookTitle + ", bookPrice=" + bookPrice + ", bookAuthor=" +
	 * bookAuthor + ", bookDescription=" + bookDescription + ", bookCover=" +
	 * bookCover + ", bookDiscount=" + bookDiscount + ", bookQuantity=" +
	 * bookQuantity + ", seller=" + seller + "]"; }
	 */

	// @Pattern(regexp = "^[0-9]$|^[1-9][0-9]$|^(100)$", message = "please enter
	// valid discount in % from")
	private int bookDiscount;
//	@Pattern(regexp = "^(0|[1-9][0-9]*)$", message = "Book Quantity is nessesary..")
	private int bookQuantity;

	// many books can belongs to one user but one book can not belong to many user
	// user->seller or buyer
	@ManyToOne
	private User seller;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "favoriteBook")
	private List<FavoriteBooks> favoriteBook = new ArrayList<>();

	/*
	 * @OneToOne(cascade = CascadeType.ALL)
	 * 
	 * @JoinColumn(name = "Book_rate_id") private BookRating bookRating;
	 */

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "book")
	private List<BookRating> bookRating = new ArrayList<>();

	/*
	 * @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
	 * "book") private List<BookReview> Reviews = new ArrayList<>();
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orderedBooks")
	private List<BookOrder> bookOrders = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bookCategory")
	private List<BookCategorys> Category = new ArrayList<>();

	public Book() {
	};

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public boolean isSale() {
		return sale;
	}

	public void setSale(boolean sale) {
		this.sale = sale;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public List<BookCategorys> getCategory() {
		return Category;
	}

	public void setCategory(List<BookCategorys> category) {
		Category = category;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public String getBookDescription() {
		return bookDescription;
	}

	public void setBookDescription(String bookDescription) {
		this.bookDescription = bookDescription;
	}

	public String getBookCover() {
		return bookCover;
	}

	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	/*
	 * public List<BookReview> getReviews() { return Reviews; }
	 * 
	 * public void setReviews(List<BookReview> reviews) { Reviews = reviews; }
	 */

	public int getBookQuantity() {
		return bookQuantity;
	}

	public void setBookQuantity(int bookQuantity) {
		this.bookQuantity = bookQuantity;
	}

	public List<BookOrder> getBookOrders() {
		return bookOrders;
	}

	public void setBookOrders(List<BookOrder> bookOrders) {
		this.bookOrders = bookOrders;
	}

	public List<FavoriteBooks> getFavoriteBook() {
		return favoriteBook;
	}

	public void setFavoriteBook(List<FavoriteBooks> favoriteBook) {
		this.favoriteBook = favoriteBook;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getBookLanguage() {
		return bookLanguage;
	}

	public void setBookLanguage(String bookLanguage) {
		this.bookLanguage = bookLanguage;
	}

	public int getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(int bookPrice) {
		this.bookPrice = bookPrice;
	}

	public int getBookDiscount() {
		return bookDiscount;
	}

	public void setBookDiscount(int bookDiscount) {
		this.bookDiscount = bookDiscount;
	}

	public boolean isBestOfYear() {
		return isBestOfYear;
	}

	public void setBestOfYear(boolean isBestOfYear) {
		this.isBestOfYear = isBestOfYear;
	}

	public int getStoreId() {
		return storeId;
	}

	public List<BookRating> getBookRating() {
		return bookRating;
	}

	public void setBookRating(List<BookRating> bookRating) {
		this.bookRating = bookRating;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	/*
	 * public String getISBN() { return ISBN; }
	 * 
	 * public void setISBN(String iSBN) { ISBN = iSBN; }
	 */
}
