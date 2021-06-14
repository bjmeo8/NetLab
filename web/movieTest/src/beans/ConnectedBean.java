package beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	private String search;
	private Movie movie;
	private String valButton;
	private Integer rating;
	private ArrayList<Movie> itemItem ;
	private ArrayList<Movie> movies;
	private ArrayList<Movie> history;
	private ArrayList<Movie> userUser;

	public ConnectedBean() {
		super();
	}
	
	
	public ConnectedBean(String valButton, Integer rating) {
		super();
		this.valButton = valButton;
	}
	
	


	public ConnectedBean(Movie movie, String valButton, Integer rating, ArrayList<Movie> itemItem) {
		super();
		this.movie = movie;
		this.valButton = valButton;
		this.rating = rating;
		this.itemItem = itemItem;
	}


	public String getSearch() {
		return search;
	}


	public void setSearch(String search) {
		this.search = search;
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
	
	
	
	
	public ArrayList<Movie> getMovies() {
		return movies;
	}

	
	
	
	
	
	
	public ArrayList<Movie> getUserUser() {
		return userUser;
	}


	public void setUserUser(ArrayList<Movie> userUser) {
		this.userUser = userUser;
	}


	public void setMovies(ArrayList<Movie> movies) {
		this.movies = movies;
	}


	public String getMovieInfos() {
		System.out.println("hi am hehre");
		System.out.println(valButton);
		setValButton(Persistance.getMovieinfo(valButton).getTitle());
		setMovie(Persistance.getMovieinfo(valButton));
		return "movieInfo";
		
	}
	
	public String getMovieInfos2() {
		System.out.println("hi am hehre2");
		System.out.println("8888888" + valButton);
		setValButton(Persistance.getMovieinfo(valButton).getTitle());
		System.out.println("6666666666 : "+ this.valButton );
		setMovie(Persistance.getMovieinfo(valButton));
		return "movieInfo2";
	}
	
	 


	public Integer getRating() {
		return rating;
	}


	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	

    public ArrayList<Movie> getItemItem() {
		return itemItem;
	}


	public void setItemItem(ArrayList<Movie> itemItem) {
		this.itemItem = itemItem;
	}


	public ArrayList<Movie> getHistory() {
		return history;
	}


	public void setHistory(ArrayList<Movie> history) {
		this.history = history;
	}


	public void onrate(RateEvent rateEvent) throws Exception {
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
        System.out.println(movie.getId());
        System.out.println(movie.getDataSource());
        //Persistance.persistRating(movie.getTitle(), userId, rating);
        String realUser = Persistance.getRealUserID(userId);
        int movieID = Integer.parseInt(movie.getId());
        
        String realIdMovie = Persistance.getRealMovieID(movieID);
        
        Persistance.persistRating(realUser, realIdMovie, rating,movie.getDataSource());
        System.out.println("ya khrrrrrrrrrrrrrrraaaaaaaa");
    }
    
    public void oncancel() {
    	System.out.println("hihihihih");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancel Event", "Rate Reset");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public String useAlgoItemItem() throws Exception {
    	System.out.println("-------------------------------------------");
    	ArrayList<Movie> tab = new ArrayList<Movie>();
    	int  userId = Integer.parseInt(SessionUtils.getUserId());
    	System.out.println("------------------"+ userId + "-----------------");
    	tab = Persistance.getAallMovieOfUser2(userId) ;
    	String source = Persistance.getuserSource(userId);
    	String stringUserID = String.valueOf(userId);
    	System.out.println("================="+ source + "=============");
    	System.out.println("================="+ stringUserID + "=============");
    	String realUser = Persistance.getRealUserID(userId);
    	System.out.println("============" + realUser + "==========");

    	/*
    	setItemItem(Persistance.getAallMovieOfUser2(userId)); 	
    	//return tab;
    	String history = "";
    	for (int i = 0; i < this.itemItem.size(); i++) {
    		if (itemItem.size()- i == 1) {
    		history = history +  this.itemItem.get(i).getTitle();
    		}
    		else {
    		history = history +  this.itemItem.get(i).getTitle()+ ",";
    		}
		}*/
    	//System.out.println("this is history"+ history);
    	String rocoMovie = getRoco(realUser,source);
    	//System.out.println("hi hi "+rocoMovie);
    	//String x = "['Last Orders', 'The Last Detail', 'The Love Letter', 'French Kiss', 'Small Time Crooks', 'Prelude to a Kiss', 'Head of State', 'Just a Kiss', 'The Last Castle', 'About Last Night...']";;
    	//System.out.println(x);
    	//String strNew = x.replaceFirst("[", "");
    	//System.out.println(strNew);
    	/*
    	String replace = rocoMovie.replace("[","");
    	 System.out.println(replace);
    	 String replace1 = replace.replace("]","");
    	 System.out.println(replace1);
    	 String replace2 = replace1.replace("'","");
		*/
    	 
    	 List<String> myList = new ArrayList<String>(Arrays.asList(rocoMovie.split(";")));
    	 //System.out.println("---" +myList.get(0).trim()+ "-----");
    	 this.movies = new ArrayList<Movie>();
    	 for (int i = 1; i< myList.size();i++) {
    		 //myList.get(i).trim();
    		 String title = myList.get(i).trim();
    		 //System.out.println("&&&&&&&&&&&&&  "+ title + "   &&&&&&&&&&&&&&");
    		 //String re = title.replaceAll("'", "\\\\'");
    		 System.out.println("===le titile==========="+title+"=============");
        	 Movie movie = Persistance.getMovieBytitle(title);
        	 //System.out.println(movie.getTitle());
        	 this.movies.add(movie);
        	 //Tropa de Elite (troupe d'élite)
    	 }
    	 

    	
    	
    	
    	
    	return "itemitem";
    	
    	
    	
    	
    	
    	
    }
    
    
    private  String getRoco(String idUser,String source) throws IOException {
		String arg1 = idUser;
		String arg2 = source;
	    String[] cmd = {
	    	      "python",
	    	      "D:\\PYTHON IDE\\\\communicationJAVAPYthon\\item_item.py",
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
    
    public String getHistorique() throws Exception {
    	String  userId = SessionUtils.getUserId();
    	int userIDInt = Integer.parseInt(userId);
    	System.out.println(userIDInt);
    	String realUser = Persistance.getRealUserID(userIDInt);
    	System.out.println(realUser);
    	

    	System.out.println("------------------"+ userId + "-----------------");
    	ArrayList<Movie> history = Persistance.gethistorique(realUser) ;
    	setHistory(history);    	
    	return "history";
    	
    		
    }
    
    
    
    private String getRocoUserUser(String iduser, String source) throws IOException {
    	String arg1 = iduser;
    	String arg2 = source;
    	   String[] cmd = {
    	         "python",
    	         "D:\\PYTHON IDE\\communicationJAVAPYthon\\user_user.py",
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
    	         x += s ;
    	         
    	         System.out.println(rr);
    	         }
    	     return x;
    	    }

    public String useAlgoUserUser() throws Exception {
        System.out.println("-------------------------------------------");
        String  userId = SessionUtils.getUserId();
        String source = Persistance.getSource(userId);
        System.out.println("------------------"+ userId + "-----------------");

        System.out.println("------------------"+ source + "-----------------");
        String rocoMovie = getRocoUserUser(userId, source);
        System.out.println(rocoMovie+"test");
       
        if (rocoMovie.equals("No Recommendation!")) {
       
        return useAlgoItemItem2(userId);
            //return "login";
       
        } else {

        List<String> myList = new ArrayList<String>(Arrays.asList(rocoMovie.split(";")));
        this.userUser = new ArrayList<Movie>();
        for (int i = 0; i< myList.size();i++) {
        String title = myList.get(i).trim();
        String re = title.replaceAll("'", "\\\\'");
        System.out.println("=============="+re+"=============");
            Movie movie = Persistance.getMovieBytitle(re);
            System.out.println(movie.getTitle()+"smya dlfilm");
            this.userUser.add(movie);    
            //Tropa de Elite (troupe d'élite)
        }
       
       
       
       
        return "useruser";
       
        }
    }
        
        public String useAlgoItemItem2(String userid) throws Exception {
            int  userId = Integer.parseInt(SessionUtils.getUserId());
            System.out.println("------------------"+ userId + "-----------------");
            String source = Persistance.getSource(userid);
            String stringUserID = String.valueOf(userId);
            String realUser = Persistance.getRealUserID(userId);
            System.out.println("============" + realUser + "==========");
            String rocoMovie = getRoco(realUser,source);


           
            List<String> myList = new ArrayList<String>(Arrays.asList(rocoMovie.split(";")));
            //System.out.println("---" +myList.get(0).trim()+ "-----");
            this.movies = new ArrayList<Movie>();
            for (int i = 1; i< myList.size();i++) {
            //myList.get(i).trim();
            String title = myList.get(i).trim();
            //System.out.println("&&&&&&&&&&&&&  "+ title + "   &&&&&&&&&&&&&&");
            //String re = title.replaceAll("'", "\\\\'");
            System.out.println("===le titile==========="+title+"=============");
                Movie movie = Persistance.getMovieBytitle(title);
                //System.out.println(movie.getTitle());
                this.movies.add(movie);
                //Tropa de Elite (troupe d'élite)
            }
           

           
           
           
           
            return "itemitem";
           
           
           
           
           
           
            }
        
        
        public List<String> completeTheme(String query) throws SQLException {
            System.out.println(query);
            System.out.println("rani hna ");
                String queryLowerCase = query.toLowerCase();
                System.out.println("rani hna ");
                ArrayList<Movie> allThemes =Persistance.getMovies(query);
                System.out.println("rani hna ");
                ArrayList<String> listitle = new ArrayList<>();
                System.out.println("rani hna ");
                for (int i = 0; i < allThemes.size(); i++) {
                listitle.add(allThemes.get(i).getTitle());
        }
                System.out.println("rani hna ");
                return listitle.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

            }


    
    
    
    
    
    
    
    
    
    public static void main(String[] args) throws Exception {
	//	System.out.println(ConnectedBean.getRoco("196","kaggle"));
	}
    
}
