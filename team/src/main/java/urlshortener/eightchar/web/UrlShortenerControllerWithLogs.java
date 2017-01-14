package urlshortener.eightchar.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;

import java.util.Map;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	//This is necessary because of the nature of SoringBoot. Not necessary if include Spring MVC
	private String login = "<html>\n" +
							"<head></head>\n" +
							"<body>\n" +
							"   <h1>Login</h1>\n" +
							"   <form name='f' action=\"admin\" method='POST'>\n" +
							"      <table>\n" +
							"         <tr>\n" +
							"            <td>User:</td>\n" +
							"            <td><input type='text' name='username' value=''></td>\n" +
							"         </tr>\n" +
							"         <tr>\n" +
							"            <td>Password:</td>\n" +
							"            <td><input type='password' name='password' /></td>\n" +
							"         </tr>\n" +
							"         <tr>\n" +
							"            <td><input name=\"submit\" type=\"submit\" value=\"submit\" /></td>\n" +
							"         </tr>\n" +
							"      </table>\n" +
							"  </form>\n" +
							"</body>\n" +
							"</html>";

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Override
	@RequestMapping(value = "/{id:(?!link|index|config|login|admin).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);

		return super.redirectTo(id, request);
	}

	@Override
	@RequestMapping(value = "/config", method = RequestMethod.POST) //Should be PUT but HTML forms doesnt support it :(
	public void configMetricsEndpoints(@RequestParam("metric") String metric){
		logger.info("Requested new configuration switch: " + metric );
		super.configMetricsEndpoints(metric);
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET) //Dumb method to get the authentication window
	public ResponseEntity<String> login(){
		logger.info("Requested log in");
		return new ResponseEntity<String>(login, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, request);
	}


}
