package model;

import java.util.ArrayList;

public class User {
	private int id;
    private String name;
    private String password;
    private String mail;
    private String genres;
    private String user_type;
	public User(int id, String name, String password, String mail, String genres) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.genres = genres;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getGenres() {
		return genres;
	}
	public void setGenres(String genres) {
		this.genres = genres;
	}
	
	
	
	
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", mail=" + mail + ", genres=" + genres
				+ "]";
	}

   
    

 

}