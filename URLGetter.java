import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;import java.net.URLConnection;
import java.util.ArrayList;import java.util.Scanner;

/** 
 * * Class that will use HTTP to get the contents of a page. 
 * * @author swapneel * 
 * */
public class URLGetter {
	private URL url;
	private HttpURLConnection httpConnection;
	public URLGetter(String url) {
		try {
			this.url = new URL(url);
			URLConnection connection = this.url.openConnection();
			httpConnection = (HttpURLConnection) connection;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void printStatusCode() { 
	    try {
	        int code = httpConnection.getResponseCode();
	        String message = httpConnection.getResponseMessage();
	        System.out.println(code + " : " + message);
	        if (code >= 300) {
	            getRedirectURL();
	        }
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}

	public void getRedirectURL() {
		try {
			//httpConnection.connect();
			String location = httpConnection.getHeaderField("Location");
			System.out.println(location);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** * The method will return the contents of the page * @return the arraylist of strings for each line on the page */
	public ArrayList<String> getContents() {
		ArrayList<String> contents = new ArrayList<String>();
		try {
			Scanner in = new Scanner(httpConnection.getInputStream());
			while(in.hasNextLine()) {
				String line = in.nextLine();contents.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}
	
	public static void main(String[] args) {
		URLGetter url = new URLGetter("https://www.nytimes.com/");
		ArrayList<String> contents = url.getContents();
		for (String e: contents) {
			System.out.println(e);
		}
	}
}