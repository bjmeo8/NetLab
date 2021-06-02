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
		user1.setGenres(genres);
		String user_id = getRandomId_user();
		String user_type = "real_user";
		try {

			String insertAddressQuery = "INSERT INTO user (name_user,password_user,mail_user,user_genre,id_user,user_type) VALUES (?,?,?,?,?,?)";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(insertAddressQuery);

			// Set values of parameters in the query.
			preparedStatement.setString(1, user1.getName());
			preparedStatement.setString(2, user1.getPassword());
			preparedStatement.setString(3, user1.getMail());
			preparedStatement.setString(4, user1.getGenres());
			preparedStatement.setString(5, user_id);
			preparedStatement.setString(6, user_type);

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

	public static ArrayList<Movie> getMoviesOfUser() {
		// System.out.println(searchMovieByImdb("tt0114885"));

		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
			String query = "SELECT title, imdb_id ,poster_path ,data_source FROM movie WHERE data_source = 'kaggle' or data_source = 'allocine' ORDER BY RAND() LIMIT 20";
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
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

	public static void persistRating(int userId, String movieName, Integer rating1, String dataSource) {
		System.out.println("rani f persistance");

		Rating rating = new Rating(movieName, userId, rating1);

		try {

			String insertAddressQuery = "INSERT INTO rating (movie_id,user_id,rating,source) VALUES (?,?,?,?)";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(insertAddressQuery);

			// Set values of parameters in the query.
			preparedStatement.setString(1, rating.getMovieName());
			preparedStatement.setInt(2, rating.getUserId());
			preparedStatement.setInt(3, rating.getRating());
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
	
	public static void main(String[] args) throws SQLException {
		/*
	ArrayList<String > array = Persistance.getAallMovieOfUser2(1138);
	
	for (int i = 0; i < array.size(); i++) {
		System.out.println(array.get(i));
	}
	*/
	Movie m = Persistance.getMovieBytitle("Tropa de Elite (troupe d\'élite)");
	System.out.println(m.getTitle());
	System.out.println(m.getProduction_companies());
		
	}

}
