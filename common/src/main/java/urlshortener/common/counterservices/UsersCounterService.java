package urlshortener.common.counterservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsersCounterService{

    private final CounterService counterService;

    @Autowired
    public UsersCounterService(CounterService counterService) {
        this.counterService = counterService;
    }

    public void incrementUsers() {
        this.counterService.increment("counter.users.registered");
    }

}
