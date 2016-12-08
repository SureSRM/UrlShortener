package urlshortener.common.web;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.cache.CacheBuilder;

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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
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
	private static final RateLimiter throttler = RateLimiter.create(10.0); // Para las limitaciones de acceso
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
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

		ShortURL su = createAndSaveIfValid(url, sponsor, UUID.randomUUID().toString(), extractIP(request));
		if (throttler.tryAcquire()){
			if (su != null) {
				HttpHeaders h = new HttpHeaders();
				h.setLocation(su.getUri());
				// Comprueba la conexion sea 200
				ResponseEntity<ShortURL> rEntity = new ResponseEntity<>(su, h, HttpStatus.OK);
				// Comprueba si la ResponseEntity es valida
				if(rEntity.getStatusCode() == HttpStatus.OK ){
					System.out.println("CONEXION VALIDA !!!!!!!!");
					// La conexion es 200, es valida la conexion
					return new ResponseEntity<>(su, h, HttpStatus.CREATED);
				} else {
					System.out.println("conexion NO valida !!!!!!!!");
					// La conexion no es valida
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}else{
			return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
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
