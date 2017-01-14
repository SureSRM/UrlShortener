package urlshortener.common.web;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.RateLimiter;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.domain.Click;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {
	private static final Logger LOG = LoggerFactory.getLogger(UrlShortenerController.class);
	private static final RateLimiter throttler = RateLimiter.create(10.0);
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link|config).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null) {
			createAndSaveClick(id, extractIP(request));
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String hash, String ip) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), null, null, null, ip, null);
		cl = clickRepository.save(cl);
		LOG.info(cl != null?"[" + hash + "] saved with id [" + cl.getId() + "]":"[" + hash + "] was not saved");
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		boolean valid = urlValidator.isValid(url) ;
		if(throttler.tryAcquire()){
			if (valid) {
				String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
				ShortURL shortUrl = shortURLRepository.findByKey(id);
				if (shortUrl!=null){
					if (shortUrl.getSafe()){
						HttpHeaders h = new HttpHeaders();
						/////shortURL.getUri() es nula.
						h.setLocation(shortUrl.getUri());
						return new ResponseEntity<>(shortUrl, h, HttpStatus.OK);
					}else{
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					}
				}else{
					return checkURL(url, sponsor, request, true);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
	}
	
	public ResponseEntity checkURL(String url, String sponsor, HttpServletRequest request, boolean firstTime){
		HttpURLConnection connection = null;
		int statusCode = 500;
		try {
			URL urlObject = new URL(url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			statusCode = connection.getResponseCode();
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (statusCode == HttpStatus.OK.value()) {
			if (firstTime){
				ShortURL su = createAndSaveIfValid(url, sponsor, UUID.randomUUID().toString(), extractIP(request));
				HttpHeaders h = new HttpHeaders();
				h.setLocation(su.getUri());
				return new ResponseEntity<>(su, h, HttpStatus.CREATED);
			}else{
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/config", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> configMetricsEndpoints(@RequestParam("metrics") Map<String, Boolean> newCOnfig){

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	private ShortURL createAndSaveIfValid(String url, String sponsor, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

		boolean valid = urlValidator.isValid(url) ;
		if (valid) {
			String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
			ShortURL su = new ShortURL(id, url,
					linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri(),
					sponsor, new Date(System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}
}
