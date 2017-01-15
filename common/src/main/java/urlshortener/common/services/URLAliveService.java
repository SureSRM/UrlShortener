package urlshortener.common.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.common.domain.ShortURL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;

@Service
public class URLAliveService{

	@Autowired
	protected ShortURLRepository shortURLRepo;

	private final long page_size = 5;
	private long current_page = 0;
	private boolean updating = false;
	
	@Scheduled( fixedRate = 500 )
	public void checkURLSAlive (){
		if (updating){
			return;
		}
		updating = true;
		ArrayList<ShortURL> urls = new ArrayList<ShortURL>(shortURLRepo.list(page_size, current_page*page_size));
		for(ShortURL url: urls){
			if (url.getSafe() != checkStatus(url)){
				url.setSafe(!url.getSafe());
				url.setCreated(new Date(System.currentTimeMillis()));
				shortURLRepo.update(url);
			}
		}

		//It iterates or resets the page counter
		if(urls.size()==page_size){
			current_page++;
		} else {
			current_page=0;
		}

		updating = false;
	}
	
	public boolean checkStatus(ShortURL shorturl){

		HttpURLConnection connection = null;
		int statusCode = 500;
		try {
			URL urlObject = new URL( shorturl.getTarget());
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			statusCode = connection.getResponseCode();

			if( HttpStatus.valueOf(statusCode).is2xxSuccessful() || HttpStatus.valueOf(statusCode).is3xxRedirection()  ){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}
}
	