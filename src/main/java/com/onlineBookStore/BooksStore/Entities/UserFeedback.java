package com.onlineBookStore.BooksStore.Entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class UserFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int feedbackId;
	@NotBlank(message = "address field not be a blank!!")
	@Column(length = 1000)
	private String feedbackStr;
	private LocalDate date;
	private int rate;
	@OneToOne
	private User fpuser;

	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}

	public String getFeedbackStr() {
		return feedbackStr;
	}

	public void setFeedbackStr(String feedbackStr) {
		this.feedbackStr = feedbackStr;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public User getFpuser() {
		return fpuser;
	}

	public void setFpuser(User fpuser) {
		this.fpuser = fpuser;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
