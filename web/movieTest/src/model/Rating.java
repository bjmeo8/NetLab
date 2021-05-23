package model;

public class Rating {
	
	private String movieName;
	private int userId;
	private Integer rating;
	public Rating(String movieName, int userId, Integer rating) {
		super();
		this.movieName = movieName;
		this.userId = userId;
		this.rating = rating;
	}
	public Rating() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getMovieName() {
		return movieName;
	}
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	@Override
	public String toString() {
		return "Rating [movieName=" + movieName + ", userId=" + userId + ", rating=" + rating + "]";
	}

	
}
	