package com.onlineBookStore.BooksStore.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class OrderPackage {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int orderPackageId;
	private String bookIsbnNumber;
	private String deliveryPerson;

	@ManyToOne
	private BookOrder order;

	public String getDeliveryPerson() {
		return deliveryPerson;
	}

	public void setDeliveryPerson(String deliveryPerson) {
		this.deliveryPerson = deliveryPerson;
	}

	public int getOrderPackageId() {
		return orderPackageId;
	}

	public void setOrderPackageId(int orderPackageId) {
		this.orderPackageId = orderPackageId;
	}

	public String getBookIsbnNumber() {
		return bookIsbnNumber;
	}

	public void setBookIsbnNumber(String bookIsbnNumber) {
		this.bookIsbnNumber = bookIsbnNumber;
	}

	public BookOrder getOrder() {
		return order;
	}

	public void setOrder(BookOrder order) {
		this.order = order;
	}

}
