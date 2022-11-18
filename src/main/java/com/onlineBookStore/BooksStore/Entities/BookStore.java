package com.onlineBookStore.BooksStore.Entities;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class BookStore {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int storeId;
	@NotBlank(message = "This field is nessesary...")
	private String storeName;
	@NotBlank(message = "This field is nessesary...")
	@Size(min = 3, max = 40, message = "Enter valid district name ....")
	private String storeDis;
	private int storeRate;
	@NotBlank(message = "This field is nessesary...")
	@Size(min = 5, max = 100, message = "Enter valid address name ....")
	private String storeAddr;
	private LocalDate enddate;
	private LocalDate startdate;
	private boolean onlinePayment;
	private boolean delivery;
	// @ColumnDefault(value = "default.png")
	private String storePic;
	private String storeTime;
	private String RZkeyId;
	private String RZkeySecret;
	private boolean validity;
	private boolean checkByAdmin;
	private float placeOrderPrice;
	private float totalEarnings;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "store_owner_id")
	private User owner;

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreDis() {
		return storeDis;
	}

	public void setStoreDis(String storeDis) {
		this.storeDis = storeDis;
	}

	public int getStoreRate() {
		return storeRate;
	}

	public void setStoreRate(int storeRate) {
		this.storeRate = storeRate;
	}

	public String getStoreAddr() {
		return storeAddr;
	}

	public void setStoreAddr(String storeAddr) {
		this.storeAddr = storeAddr;
	}

	public LocalDate getEnddate() {
		return enddate;
	}

	public void setEnddate(LocalDate localDate) {
		this.enddate = localDate;
	}

	public boolean isOnlinePayment() {
		return onlinePayment;
	}

	public void setOnlinePayment(boolean onlinePayment) {
		this.onlinePayment = onlinePayment;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getStorePic() {
		return storePic;
	}

	public void setStorePic(String storePic) {
		this.storePic = storePic;
	}

	public String getStoreTime() {
		return storeTime;
	}

	public void setStoreTime(String storeTime) {
		this.storeTime = storeTime;
	}

	public String getRZkeyId() {
		return RZkeyId;
	}

	public void setRZkeyId(String rZkeyId) {
		RZkeyId = rZkeyId;
	}

	public String getRZkeySecret() {
		return RZkeySecret;
	}

	public void setRZkeySecret(String rZkeySecret) {
		RZkeySecret = rZkeySecret;
	}

	public boolean isValidity() {
		return validity;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	public boolean isCheckByAdmin() {
		return checkByAdmin;
	}

	public void setCheckByAdmin(boolean checkByAdmin) {
		this.checkByAdmin = checkByAdmin;
	}

	public float getPlaceOrderPrice() {
		return placeOrderPrice;
	}

	public void setPlaceOrderPrice(float placeOrderPrice) {
		this.placeOrderPrice = placeOrderPrice;
	}

	public float getTotalEarnings() {
		return totalEarnings;
	}

	public void setTotalEarnings(float totalEarnings) {
		this.totalEarnings = totalEarnings;
	}

	public LocalDate getStartdate() {
		return startdate;
	}

	public void setStartdate(LocalDate startdate) {
		this.startdate = startdate;
	}

	/*
	 * @Override public String toString() { return "BookStore [storeId=" + storeId +
	 * ", storeName=" + storeName + ", storeDis=" + storeDis + ", storeRate=" +
	 * storeRate + ", storeAddr=" + storeAddr + ", enddate=" + enddate +
	 * ", onlinePayment=" + onlinePayment + ", delivery=" + delivery + ", storePic="
	 * + storePic + ", storeTime=" + storeTime + ", owner=" + owner + "]"; }
	 */

}
