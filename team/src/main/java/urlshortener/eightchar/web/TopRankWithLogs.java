package urlshortener.eightchar.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.common.domain.RankPosition;
import urlshortener.common.repository.ClickRepository;

import java.util.List;

/**
 * Created by sergio on 13/11/16.
 */
@RestController
public class TopRankWithLogs extends urlshortener.common.web.TopRank{

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public @ResponseBody List<RankPosition> giveRank( ) {
        logger.info("Requested rank counterservices");
        return super.giveRank();
    }
}
