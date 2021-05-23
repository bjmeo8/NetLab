package restApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import java.net.URL;
import java.net.URLEncoder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ImagePoster {
	 
	public static final String SEARCH_URL_TITLE = "http://www.omdbapi.com/?s=TITLE&apikey=89efbed7&r=xml";
	public static final String SEARCH_URL_IMDB =  "http://www.omdbapi.com/?i=IMDB&apikey=89efbed7&plot=full&r=xml";

	public static String sendGetRequests(String requestUrl)  {
		StringBuffer response = new StringBuffer();
		try {
		URL url = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Content-Type", "application/xml; charset=UTF-8 ");
		InputStream stream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		String ligne = "";
		while( (ligne = buffer.readLine()) != null) {
			response.append(ligne);
		}
		buffer.close();
		connection.disconnect(); 
		} catch (IOException e) { e.printStackTrace(); }
		
		return response.toString(); 
	} 
	
	public static String searchMovieByTitle(String title) throws Exception  {
		title = URLEncoder.encode(title,"UTF-8");
        String requestUrl = SEARCH_URL_TITLE .replaceAll("TITLE",title);
		return sendGetRequests(requestUrl);
		}

	public static String searchMovieByImdb(String imdb) {
		String requestUrl = SEARCH_URL_IMDB .replaceAll("IMDB",imdb);
		return sendGetRequests(requestUrl);
        }    
    public static String getMovieImdbId(String movie_name) throws Exception {
    
    	String movie_xml_content = searchMovieByTitle(movie_name);
        DocumentBuilderFactory documentumentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentumentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentumentBuilder = documentumentBuilderFactory.newDocumentBuilder();
        Document document = documentumentBuilder.parse(new InputSource(new StringReader(movie_xml_content)));
        String XPATH_QUERY = "/root/result/@imdbID";
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile(XPATH_QUERY);
        String query_result = (String) expr.evaluate(document, XPathConstants.STRING);
        return query_result;    	
    }
        
    public static String getMoviePosterByImdbID(String movie_id) throws Exception {

    	//String movie_id = getMovieImdbId(movie_name);
    	String movie_xml_content = searchMovieByImdb(movie_id);
        DocumentBuilderFactory documentumentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentumentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentumentBuilder = documentumentBuilderFactory.newDocumentBuilder();
        Document document = documentumentBuilder.parse(new InputSource(new StringReader(movie_xml_content)));
        String XPATH_QUERY = "/root/movie/@poster";
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile(XPATH_QUERY);
        String query_result = (String) expr.evaluate(document, XPathConstants.STRING);
        return query_result;    	
    }
 
	public static void main(String[] args) throws Exception {
		System.out.println(getMoviePosterByImdbID("zeno"));
		//System.out.println(searchMovieByImdb("tt0114885"));
	}

}
