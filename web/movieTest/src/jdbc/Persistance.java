package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import model.User;
import restApi.OmdbApi;
import model.Movie;
import model.Rating;

public class Persistance {

	public static void persistUser(String name, String password, String mail, String genres) {
		// User(String name, String password, String mail, String genres)
		User user1 = new User();
		user1.setName(name);
		user1.setPassword(password);
		user1.setMail(mail);
		user1.setUser_type(genres);
		String user_id = getRandomId_user();
		String user_type = genres;
		try {

			String insertAddressQuery = "INSERT INTO user (name_user,password_user,mail_user,id_user,user_type,user_genre) VALUES (?,?,?,?,?,?)";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(insertAddressQuery);

			// Set values of parameters in the query.
			preparedStatement.setString(1, user1.getName());
			preparedStatement.setString(2, user1.getPassword());
			preparedStatement.setString(3, user1.getMail());
			//preparedStatement.setString(4, user1.getGenres());
			preparedStatement.setString(4, user_id);
			preparedStatement.setString(5, user_type);
			
			preparedStatement.setString(6, "default");


			preparedStatement.executeUpdate();

			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}

	public static Boolean getUser(String username, String password) {

		User user = new User();
		user.setName(username);
		user.setPassword(password);
		try {

			String selectUserQuery = "SELECT * FROM user AS u WHERE u.name_user = ?  AND u.password_user = ?";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			// preparedStatement.setString(3, user.getChoises());

			ResultSet result = preparedStatement.executeQuery();

			if (result.next()) {

				user.setName(result.getString("name_user"));
				user.setPassword(result.getString("password_user"));
				user.setMail(result.getString("mail_user"));
				user.setGenres(result.getString("user_genre"));

				return true;
			}

			preparedStatement.close();

		} catch (SQLException se) {
			System.err.println(se.getMessage());

			return false;
		}
		return false;
	}

	public static ArrayList<Movie> getMoviesOfUser(String source,int taille) {
		// System.out.println(searchMovieByImdb("tt0114885"));

		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
			String query = "SELECT * FROM (SELECT * FROM movie m WHERE m.data_source = ?  LIMIT ? ) as a where a.data_source= ?  ORDER BY RAND ( )  LIMIT 10";
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
			preparedStatement.setString(1, source);
			preparedStatement.setInt(2, taille);
			preparedStatement.setString(3, source);



			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Movie movie = new Movie();
				movie.setTitle(rs.getString("title"));
				movie.setDataSource(rs.getString("data_source"));
				String dataSource = rs.getString("data_source");
				String posterpath = "";
				if (dataSource.contentEquals("kaggle")) {
					String imdb_id = rs.getString("imdb_id");
					String posterbyImdbid = OmdbApi.getMoviePosterByImdbID(imdb_id);
					if (!posterbyImdbid.contentEquals("")) {
						posterpath = posterbyImdbid;
					} else {
						posterpath = "C:\\j2Eclipse\\workspace_Jee\\movieTest\\WebContent\\resources\\images\\netflix-image.jpg";
					}

					movie.setPoster_path(posterpath);

				} else {
					String posterAlloCine = rs.getString("poster_path");
					posterpath = posterAlloCine;
					movie.setPoster_path(posterpath);

				}

				movies.add(movie);
			}
			preparedStatement.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return movies;
	}

	public static String getGenre(String name) throws Exception {
		String genre = "";
		String query = "SELECT user_genre FROM user where name_user = '" + name + "'";
		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			// Movie movie = new Movie();
			genre += rs.getString("user_genre");
		}
		preparedStatement.close();
		return genre;

	}

	public static int getIDUSer(String username, String password) throws Exception {
		User user = new User();
		String selectUserQuery = "SELECT * FROM user AS u WHERE u.name_user = ?  AND u.password_user = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setString(1, username);
		preparedStatement.setString(2, password);
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			user.setId(result.getInt("id"));
			user.setName(result.getString("name_user"));
			user.setPassword(result.getString("password_user"));
			user.setMail(result.getString("mail_user"));
			user.setGenres(result.getString("user_genre"));

		}

		return user.getId();

	}
	
	public static String getuserSource(int userid) throws Exception {
		User user = new User();
		String source = "";
		String selectUserQuery = "SELECT * FROM rating AS u WHERE u.user_id = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setInt(1, userid);
	
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			//user.setId(result.getInt("id"));
			source = result.getString("source");

		}

		return source;

	}
	
	public static String getRealUserID(int userid) throws Exception {
		User user = new User();
		String source = "";
		String selectUserQuery = "SELECT * FROM user AS u WHERE u.id = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setInt(1, userid);
	
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			//user.setId(result.getInt("id"));
			source = result.getString("id_user");

		}

		return source;

	}
	
	public static String getRealMovieID(int movieid) throws Exception {
		String source = "";
		String selectUserQuery = "SELECT * FROM movie AS u WHERE u.id = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setInt(1, movieid);
	
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			//user.setId(result.getInt("id"));
			source = result.getString("movie_id");

		}

		return source;

	}
	
	public static String getuserType(String movieid) throws Exception {
		String source = "";
		String selectUserQuery = "SELECT * FROM user AS u WHERE u.id_user = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setString(1, movieid);
	
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			//user.setId(result.getInt("id"));
			source = result.getString("user_type");

		}

		return source;

	}
	
	
	
	
	
	public static String getSourceId(String userID) throws Exception {
		String source = "";
		String selectUserQuery = "SELECT * FROM user AS u WHERE u.id_user = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(selectUserQuery);

		preparedStatement.setString(1, userID);
	
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			//user.setId(result.getInt("id"));
			source = result.getString("user_type");

		}

		return source;

	}
	
	
	
	 public static String getSource(String iduser) throws Exception {
		 String source = "";
		 String query = "SELECT user_type FROM user where id = '" + iduser + "'";
		 Connection dbConnection = JdbcConnection.getConnection();
		 PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		 ResultSet rs = preparedStatement.executeQuery();
		 while (rs.next()) {
		 // Movie movie = new Movie();
		 source += rs.getString("user_type");
		 }
		 preparedStatement.close();
		 return source;

		 }
	 
	 
	 public static ArrayList<Movie> getMovies(String word) {
		// System.out.println(searchMovieByImdb("tt0114885"));

		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
		String query = "SELECT title,data_source FROM movie WHERE title LIKE ? and data_source = 'kaggle' or data_source = 'allocine' LIMIT 10";
		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		preparedStatement.setString(1, word+"%");
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
		Movie movie = new Movie();
		movie.setTitle(rs.getString("title"));
		movie.setDataSource(rs.getString("data_source"));

		movies.add(movie);
		}
		preparedStatement.close();
		} catch (Exception e) {
		System.out.println(e);
		}

		return movies;
		}
	
	
	
	
	
	
	

	public static void persistRating(String userId, String movieName, Integer rating1, String dataSource) {
		System.out.println("rani f persistance");

		//Rating rating = new Rating(movieName, userId, rating1);

		try {

			String insertAddressQuery = "INSERT INTO rating (movie_id,user_id,rating,source) VALUES (?,?,?,?)";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(insertAddressQuery);

			// Set values of parameters in the query.
			preparedStatement.setString(1, movieName);
			preparedStatement.setString(2, userId);
			preparedStatement.setInt(3, rating1);
			preparedStatement.setString(4, dataSource);
			preparedStatement.executeUpdate();

			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}

	public static Movie getMovieinfo(String valButton) {
		Movie movie = new Movie();
		try {
			String query = "SELECT * FROM movie where title ='" + valButton + "'";
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				movie.setId(rs.getString("id"));
				movie.setTitle(rs.getString("title"));
				movie.setOverview(rs.getString("overview"));
				movie.setProduction_companies(rs.getString("production_companies"));
				movie.setDataSource(rs.getString("data_source"));
				String idImdb = rs.getString("data_source");
				String dataSource = rs.getString("data_source");
				String posterpath = "";
				// zid l ba9i
				// movie.setIduser(rs.getString("user"));
				String imdb_id = rs.getString("imdb_id");

				if (dataSource.contentEquals("kaggle")) {
					String posterbyImdbid = OmdbApi.getMoviePosterByImdbID(imdb_id);
					if (!posterbyImdbid.contentEquals("")) {
						posterpath = posterbyImdbid;
					} else {
						posterpath = "C:\\j2Eclipse\\workspace_Jee\\movieTest\\WebContent\\resources\\images\\netflix-image.jpg";
					}

					movie.setPoster_path(posterpath);

				} else {
					String posterAlloCine = rs.getString("poster_path");
					posterpath = posterAlloCine;
					movie.setPoster_path(posterpath);

				}

				if (dataSource.contentEquals("kaggle")) {
					movie.setGenres(OmdbApi.getMovieGenreByImdbID(imdb_id));
				} else {
					String genreAllocine = rs.getString("genres");
					// System.out.println("le genre " + genreAllocine);
					movie.setGenres(genreAllocine);
				}

			}
			preparedStatement.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return movie;
	}

	/**
	 * tarek fait attention où elle appelé la methode car elle utilise ancien base
	 * /!\
	 * 
	 * @param title
	 * @return
	 */
	public static ArrayList<String> getAallMovieOfUser(int userID) throws SQLException {

		ArrayList<String> movies = new ArrayList<String>();
		String query = "SELECT * FROM rating AS u WHERE u.user_id = ?";

		Connection dbConnection = JdbcConnection.getConnection();
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);

		preparedStatement.setInt(1, userID);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Movie movie = new Movie();
			movie.setId(rs.getString("movie_id"));
			movies.add(movie.getId());
		}

		

		 return movies;

	}
	
	public static ArrayList<String> getAallMovieByID(ArrayList<String> movieID) throws SQLException {
	
		ArrayList<String> movies2 = new ArrayList<String>();
		Connection dbConnection2 = JdbcConnection.getConnection();
		for (int i = 0; i < movieID.size(); i++) {
			String query2 = "SELECT * FROM movie where id = ?";
			
			PreparedStatement preparedStatement = dbConnection2.prepareStatement(query2);
			int movieid = Integer.parseInt(movieID.get(i));
			preparedStatement.setInt(1, movieid);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Movie movie = new Movie();
				movie.setTitle(rs.getString("title"));
				movies2.add(movie.getTitle());
			}

		}
		return movies2;
	}
	
	
	
	public static ArrayList<Movie> getAallMovieOfUser2(int userID) throws SQLException {
		ArrayList<String> movieID = getAallMovieOfUser(userID);
		ArrayList<String> movietitle = getAallMovieByID(movieID);
		ArrayList<Movie> moviess = new ArrayList<Movie>();
		for (int i = 0; i< movietitle.size();i++) {
			Movie movie = new Movie();
			movie.setTitle(movietitle.get(i));
			moviess.add(movie);
		}
		return moviess;
		
	}

	
	

	/**
	 * tarek fait attention où elle appelé la methode car elle utiliser movies et
	 * pas movie /!\
	 * 
	 * @param title
	 * @return
	 */
	public static Movie getMovieBytitle(String title) {

		Movie movie = new Movie();
		try {
			String query = "SELECT * FROM movie where title = ?";
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
			preparedStatement.setString(1,title );
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {

				movie.setTitle(rs.getString("title"));
				movie.setProduction_companies(rs.getString("production_companies"));
				movie.setOverview(rs.getString("overview"));
				movie.setDataSource(rs.getString("data_source"));
				
				// zid l ba9i
				// movie.setIduser(rs.getString("user"));
				
				
				
				String posterpath = "";
				// zid l ba9i
				// movie.setIduser(rs.getString("user"));
				String imdb_id = rs.getString("imdb_id");
				String idImdb = rs.getString("data_source");
				String dataSource = rs.getString("data_source");

				if (dataSource.contentEquals("kaggle")) {
					String posterbyImdbid = OmdbApi.getMoviePosterByImdbID(imdb_id);
					if (!posterbyImdbid.contentEquals("")) {
						posterpath = posterbyImdbid;
					} else {
						posterpath = "C:\\j2Eclipse\\workspace_Jee\\movieTest\\WebContent\\resources\\images\\netflix-image.jpg";
					}

					movie.setPoster_path(posterpath);

				} else {
					String posterAlloCine = rs.getString("poster_path");
					posterpath = posterAlloCine;
					movie.setPoster_path(posterpath);

				}

				if (dataSource.contentEquals("kaggle")) {
					movie.setGenres(OmdbApi.getMovieGenreByImdbID(imdb_id));
				} else {
					String genreAllocine = rs.getString("genres");
					// System.out.println("le genre " + genreAllocine);
					movie.setGenres(genreAllocine);
				}
				
				
				
				
				
				
			}
			preparedStatement.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return movie;

	}
	
	public static ArrayList<Movie> gethistorique(String movieID) throws Exception {
		
		ArrayList<Movie> movies2 = new ArrayList<Movie>();
		Connection dbConnection2 = JdbcConnection.getConnection();
	
			String query2 = "SELECT * FROM `rating` r , `user` u , `movie` m WHERE r.user_id = u.id_user and u.id_user = ? and m.movie_id = r.movie_id ";
			
			PreparedStatement preparedStatement = dbConnection2.prepareStatement(query2);
			//int movieid = Integer.parseInt(movieID);
			preparedStatement.setString(1, movieID);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Movie movie = new Movie();				
				movie.setTitle(rs.getString("title"));
				movie.setId(rs.getString("id"));
				movie.setDataSource(rs.getString("data_source"));
				movie.setGenres(rs.getString("genres"));
				movie.setOverview(rs.getString("overview"));
				movie.setPoster_path(rs.getString("poster_path"));
				movie.setProduction_companies(rs.getString("production_companies"));
				String posterpath = "";
				// zid l ba9i
				// movie.setIduser(rs.getString("user"));
				String imdb_id = rs.getString("imdb_id");
				String idImdb = rs.getString("data_source");
				String dataSource = rs.getString("data_source");

				if (dataSource.contentEquals("kaggle")) {
					String posterbyImdbid = OmdbApi.getMoviePosterByImdbID(imdb_id);
					if (!posterbyImdbid.contentEquals("")) {
						posterpath = posterbyImdbid;
					} else {
						posterpath = "C:\\j2Eclipse\\workspace_Jee\\movieTest\\WebContent\\resources\\images\\netflix-image.jpg";
					}

					movie.setPoster_path(posterpath);

				} else {
					String posterAlloCine = rs.getString("poster_path");
					posterpath = posterAlloCine;
					movie.setPoster_path(posterpath);

				}

				if (dataSource.contentEquals("kaggle")) {
					movie.setGenres(OmdbApi.getMovieGenreByImdbID(imdb_id));
				} else {
					String genreAllocine = rs.getString("genres");
					// System.out.println("le genre " + genreAllocine);
					movie.setGenres(genreAllocine);
				}	
				
				movies2.add(movie);
			}

		
		return movies2;
	}
	
	
	
	
	
	
	
	
	
	

	public static String getRandomId_user() {
		String x = "real_user";
		Random rand = new Random();
		int minimum = 1;
		int maximum = 1000;
		int randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
		String randomNumToString = String.valueOf(randomNum);
		String user = x + "_" + randomNumToString;
		// System.out.println(user);
		return user;
	}
	
	public static void main(String[] args) throws Exception {
		/*
	ArrayList<String > array = Persistance.getAallMovieOfUser2(1138);
	
	for (int i = 0; i < array.size(); i++) {
		System.out.println(array.get(i));
	}
	*/
	//Movie m = Persistance.getMovieBytitle("Tropa de Elite (troupe d\'élite)");
	//System.out.println(m.getTitle());
	//System.out.println(m.getProduction_companies());
		/*
		ArrayList<Movie> array  = Persistance.gethistorique("real_user_70");
		for (Movie movie : array) {
			System.out.println(movie.toString());
		}
		*/
		//System.out.println(Persistance.getSourceId("Z20141218234416927428111"));
//System.out.println(getuserSource(196));	
		//System.out.println(Persistance.getuserType("Z20141218234416927428111"));
		ArrayList<Movie> arry = Persistance.getMoviesOfUser("kaggle",6000);
		for (int i = 0; i < arry.size(); i++) {
			System.out.println(arry.get(i).toString());
		}
		
	}

}
