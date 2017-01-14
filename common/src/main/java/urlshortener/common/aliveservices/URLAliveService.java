package urlshortener.common.aliveservices;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;

@Service
public class URLAliveService{

	@Autowired
	protected ShortURLRepository shortURLRepo;
	
	@Autowired
	protected UrlShortenerController urlShortenerController;
	
	private final long page_size = 5;
	private long current_page = 1;
	private boolean updating = false;
	
	@Scheduled(fixedRate = 500)
	public void checkURLSAlive (){
		if (updating){
			return;
		}
		updating = true;
		ArrayList<ShortURL> urls = new ArrayList<ShortURL>(shortURLRepo.list(page_size, current_page*page_size));
		for(ShortURL url: urls){
			if (url.getSafe() != checkStatus(url)){
				url.setSafe(!url.getSafe());
				shortURLRepo.update(url);
			}
		}
		updating = false;
	}
	
	public boolean checkStatus(ShortURL shorturl){
		ResponseEntity response = urlShortenerController.checkURL(shorturl.getTarget(), shorturl.getSponsor(), null, false);
		if (response.getStatusCode().value() == 200){
			return true;
		}else{
			return false;
		}
	}
}
	