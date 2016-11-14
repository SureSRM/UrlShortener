package urlshortener.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.common.domain.RankPosition;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sergio on 13/11/16.
 */
@RestController
public class TopRank {

    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public @ResponseBody List<RankPosition> giveRank( ) {
        return clickRepository.listTop(10);
    }
}
