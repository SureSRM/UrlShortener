package urlshortener.common.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomMetrics {

    private final CounterService counterService;

    private final GaugeService gaugeService;

    @Autowired
    public CustomMetrics(CounterService counterService, GaugeService gaugeService) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    public void incrementUsers() {
        this.counterService.increment("counter.users.registered");
    }

}
