package model;

public class Movie {

	private String id;
	private String title;
	private String genres;
	private String overview;
	private String production_companies;
	private String poster_path;
	private String dataSource;
	public Movie() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Movie(String id, String title, String genres, String overview, String production_companies,
			String poster_path) {
		super();
		this.id = id;
		this.title = title;
		this.genres = genres;
		this.overview = overview;
		this.production_companies = production_companies;
		this.poster_path = poster_path;
	}
	
	public Movie(String id, String title, String genres, String overview, String production_companies,
			String poster_path, String dataSource) {
		super();
		this.id = id;
		this.title = title;
		this.genres = genres;
		this.overview = overview;
		this.production_companies = production_companies;
		this.poster_path = poster_path;
		this.dataSource = dataSource;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenres() {
		return genres;
	}
	public void setGenres(String genres) {
		this.genres = genres;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getProduction_companies() {
		return production_companies;
	}
	public void setProduction_companies(String production_companies) {
		this.production_companies = production_companies;
	}
	public String getPoster_path() {
		return poster_path;
	}
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	@Override
	public String toString() {
		return "Movie [id=" + id + ", title=" + title + ", genres=" + genres + ", overview=" + overview
				+ ", production_companies=" + production_companies + ", poster_path=" + poster_path + "]";
	}
	
	
	
	

}
