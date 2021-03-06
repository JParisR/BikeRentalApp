package es.udc.ws.bikes.client.service.dto;

import java.util.Calendar;

public class UserClientBookDto {

	private Long bookId;
    private Long bikeId;
    private String email;
    private String creditCard;
    private Calendar startDate;
    private Calendar endDate;
    private int days;
	private int units;
    private int rating;
    
    public UserClientBookDto(Long bikeId, String email, String creditCard, Calendar startDate, 
			 Calendar endDate, int units) {
    	this.bikeId = bikeId;
    	this.email = email;
    	this.creditCard = creditCard;
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.units = units;
   
    }
    
    public UserClientBookDto(Long bookId, Long bikeId, String email, String creditCard, 
    						 Calendar startDate, Calendar endDate, int units) {	
    	this(bikeId, email, creditCard, startDate, endDate, units);
    	this.bookId = bookId;
    }
    
    public UserClientBookDto(Long bookId, Long bikeId, String email, Calendar startDate, int days, int rating) {
    	this.bookId = bookId;
    	this.bikeId = bikeId;
    	this.email = email;
    	this.startDate = startDate;
    	this.days = days;
    	this.rating = rating;
    }
    
    public UserClientBookDto(Long bookId, String email, int rating) {
    	this.bookId = bookId;
    	this.email = email;
    	this.rating = rating;
    }

	public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    
    public Long getBikeId() {
		return bikeId;
	}

	public void setBikeId(Long bikeId) {
		this.bikeId = bikeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}
	
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "UserClientBookDto [bookId=" + bookId + ", bikeId=" + bikeId + ", email=" + email + ", creditCard="
				+ creditCard + ", startDate=" + startDate + ", units=" + units + ", rating="
				+ rating + "]";
	}
	
}
