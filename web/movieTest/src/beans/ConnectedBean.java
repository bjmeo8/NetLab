package beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;


import org.primefaces.event.RateEvent;

import jdbc.Persistance;
import model.Movie;

@SessionScoped
@ManagedBean
public class ConnectedBean {
	
	private Movie movie;
	private String valButton;
	private Integer rating;
	private ArrayList<String> itemItem ;
	private ArrayList<String> movies;

	public ConnectedBean() {
		super();
	}
	
	
	public ConnectedBean(String valButton, Integer rating) {
		super();
		this.valButton = valButton;
	}
	
	


	public ConnectedBean(Movie movie, String valButton, Integer rating, ArrayList<String> itemItem) {
		super();
		this.movie = movie;
		this.valButton = valButton;
		this.rating = rating;
		this.itemItem = itemItem;
	}


	public String getValButton() {
		return valButton;
	}


	public void setValButton(String valButton) {
		this.valButton = valButton;
	}



	public Movie getMovie() {
		return movie;
	}


	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	
	
	
	public ArrayList<String> getMovies() {
		return movies;
	}


	public void setMovies(ArrayList<String> movies) {
		this.movies = movies;
	}


	public String getMovieInfos() {
		System.out.println(valButton);
		setValButton(Persistance.getMovieinfo(valButton).getTitle());
		setMovie(Persistance.getMovieinfo(valButton));
		return "movieInfo";
		
	}


	public Integer getRating() {
		return rating;
	}


	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	

    public ArrayList<String> getItemItem() {
		return itemItem;
	}


	public void setItemItem(ArrayList<String> itemItem) {
		this.itemItem = itemItem;
	}


	public void onrate(RateEvent rateEvent) {
    	System.out.println("hahahahahahahahahahahah");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rate Event", "You rated:" + ((Integer) rateEvent.getRating()).intValue());
        FacesContext.getCurrentInstance().addMessage(null, message);
        System.out.println(((Integer) rateEvent.getRating()).intValue());
        System.out.println(SessionUtils.getUserName());
        System.out.println(movie.getTitle());
        setRating(((Integer) rateEvent.getRating()).intValue());
        //String username = SessionUtils.getUserName();
        //System.out.println(username);
        int  userId = Integer.parseInt(SessionUtils.getUserId());
        System.out.println(userId);
        //Persistance.persistRating(movie.getTitle(), userId, rating);
        Persistance.persistRating(userId, movie.getTitle(), rating);
        System.out.println("ya khrrrrrrrrrrrrrrraaaaaaaa");
    }
    
    public void oncancel() {
    	System.out.println("hihihihih");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancel Event", "Rate Reset");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public String useAlgoItemItem() throws SQLException, IOException {
    	System.out.println("-------------------------------------------");
    	ArrayList<String> tab = new ArrayList<String>();
    	int  userId = Integer.parseInt(SessionUtils.getUserId());
    	System.out.println("------------------"+ userId + "-----------------");
    	tab = Persistance.getAallMovieOfUser(userId) ;
    	setItemItem(Persistance.getAallMovieOfUser(userId)); 	
    	//return tab;
    	String history = "";
    	for (int i = 0; i < this.itemItem.size(); i++) {
    		if (itemItem.size()- i == 1) {
    		history = history +  this.itemItem.get(i);
    		}
    		else {
    		history = history +  this.itemItem.get(i)+ ",";
    		}
		}
    	//System.out.println("this is history"+ history);
    	String rocoMovie = getRoco(history);
    	System.out.println(rocoMovie);
    	//String x = "['Last Orders', 'The Last Detail', 'The Love Letter', 'French Kiss', 'Small Time Crooks', 'Prelude to a Kiss', 'Head of State', 'Just a Kiss', 'The Last Castle', 'About Last Night...']";;
    	//System.out.println(x);
    	//String strNew = x.replaceFirst("[", "");
    	//System.out.println(strNew);
    	 String replace = rocoMovie.replace("[","");
    	 System.out.println(replace);
    	 String replace1 = replace.replace("]","");
    	 System.out.println(replace1);
    	 String replace2 = replace1.replace("'","");

    	 
    	 List<String> myList = new ArrayList<String>(Arrays.asList(replace2.split(",")));
    	 System.out.println("---" +myList.get(0).trim()+ "-----");
    	 this.movies = new ArrayList<String>();
    	 for (int i = 0; i< myList.size();i++) {
    		 //myList.get(i).trim();
        	 Movie movie = Persistance.getMovieBytitle(myList.get(i).trim());
        	 System.out.println(movie.getTitle());
        	 this.movies.add(movie.getTitle());

    	 }
    	 

    	
    	
    	
    	
    	return "itemitem";
    	
    	
    	
    	
    	
    	
    }
    
    
    private String getRoco(String history) throws IOException {
		String arg1 = history;
		String arg2 = "hola ";
	    String[] cmd = {
	    	      "python",
	    	      "D:\\PYTHON IDE\\communicationJAVAPYthon\\item_item.py",
	    	      arg1,arg2
	    	    };
	    	  //Runtime.getRuntime().exec(cmd);	 
	    	  Runtime r = Runtime.getRuntime();
	    	  Process p = r.exec(cmd);
	    	  BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	  BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	    	  String rr= "";
	    	  String s = "";
	    	  String x = "";
	    	  while((s = in.readLine()) != null || (rr = error.readLine()) != null ){
	    	      //System.out.println(s);
	    	      x = s ;
	    	      
	    	    //  System.out.println(rr);
	    	      }
	    	  return x;
    }
    
    
    
    
}