package beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import jdbc.Persistance;

@ManagedBean
public class SignBean implements Serializable {

	private static final long serialVersionUID = -426721429642192283L;
	

    private String name;
    private String password;
    private String mail;
    private String genres;
    
	
	public SignBean(String name, String password, String mail, String genres) {
		super();
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.genres = genres;
	}

	public SignBean() {
		super();
		// TODO Auto-generated constructor stub
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public void insertuser() {
		//Persistance.persistUser(username,password,category);
		Persistance.persistUser(name, password, mail, genres);
	}


	



}
