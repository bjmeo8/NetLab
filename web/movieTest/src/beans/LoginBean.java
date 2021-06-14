package beans;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import jdbc.Persistance;
import model.Movie;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = -426721429642192283L;
	

	private String username;
	
	private String password;
	
	private String logout;
	
	private int userid;
	
	private ArrayList<Movie> moviesList;
	
	public LoginBean() {
		
	}

	public LoginBean(String username, String password) throws Exception {
		super();
		this.username = username;
		this.password = password;
		
	}
	
	
	public ArrayList<Movie> getMoviesList() {
		return moviesList;
	}

	public void setMoviesList(ArrayList<Movie> moviesList) {
		this.moviesList = moviesList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String validateUsernamePassword() throws Exception {
		boolean valid = Persistance.getUser(username, password);
		setUserid(Persistance.getIDUSer(username, password));
		if (valid) {
			HttpSession session = SessionUtils.getSession();
			session.setAttribute("username", username);
			session.setAttribute("userid", userid);
	        String realUser = Persistance.getRealUserID(userid);
	        String source = Persistance.getuserType(realUser);	
	        System.out.println(source);
			//String genre = Persistance.getGenre(username);
			setMoviesList(Persistance.getMoviesOfUser(source,6000));
			return "connected";
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
				 new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Incorrect Username and Passowrd",
							"Please enter correct username and Password"));
			
			return "login";
		}
	}
	

	//logout event, invalidate session
	public String logoutt() {
		HttpSession session = SessionUtils.getSession();
		session.invalidate();
		return "login";
	}

	public String getLogout() {
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}
}

