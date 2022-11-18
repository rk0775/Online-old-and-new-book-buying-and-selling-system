package com.onlineBookStore.BooksStore.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class BookOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int orderId;
	private String OrderPersonName;
	private String orderContactNum;
	private String orderStatus;
	private String orderDistrict;
	private String orderPincode;
	private String oderAddr;
	private float orderPrice;
	private LocalDate orderDate;
	private String paymentType;
	private int bookQuantity;
	@ManyToOne
	private User orderedUser;

	@ManyToOne
	private Book orderedBooks;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
	private List<OrderPackage> orderPackages = new ArrayList<>();

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderPersonName() {
		return OrderPersonName;
	}

	public void setOrderPersonName(String orderPersonName) {
		OrderPersonName = orderPersonName;
	}

	public String getOrderDistrict() {
		return orderDistrict;
	}

	public void setOrderDistrict(String orderDistrict) {
		this.orderDistrict = orderDistrict;
	}

	public String getOrderPincode() {
		return orderPincode;
	}

	public String getOrderContactNum() {
		return orderContactNum;
	}

	public void setOrderContactNum(String orderContactNum) {
		this.orderContactNum = orderContactNum;
	}

	public int getBookQuantity() {
		return bookQuantity;
	}

	public void setBookQuantity(int bookQuantity) {
		this.bookQuantity = bookQuantity;
	}

	public void setOrderPincode(String orderPincode) {
		this.orderPincode = orderPincode;
	}

	public String getOderAddr() {
		return oderAddr;
	}

	public void setOderAddr(String oderAddr) {
		this.oderAddr = oderAddr;
	}

	public float getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public User getOrderedUser() {
		return orderedUser;
	}

	public void setOrderedUser(User orderedUser) {
		this.orderedUser = orderedUser;
	}

	public Book getOrderedBooks() {
		return orderedBooks;
	}

	public void setOrderedBooks(Book orderedBooks) {
		this.orderedBooks = orderedBooks;
	}

	public List<OrderPackage> getOrderPackages() {
		return orderPackages;
	}

	public void setOrderPackages(List<OrderPackage> orderPackages) {
		this.orderPackages = orderPackages;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	/*
	 * @Override public String toString() { return "BookOrder [orderId=" + orderId +
	 * ", OrderPersonName=" + OrderPersonName + ", orderContactNum=" +
	 * orderContactNum + ", orderStatus=" + orderStatus + ", orderDistrict=" +
	 * orderDistrict + ", orderPincode=" + orderPincode + ", oderAddr=" + oderAddr +
	 * ", orderPrice=" + orderPrice + ", paymentType=" + paymentType +
	 * ", bookQuantity=" + bookQuantity + ", orderedUser=" + orderedUser +
	 * ", orderedBooks=" + orderedBooks + "]"; }
	 */

}
