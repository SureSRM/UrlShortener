package urlshortener.eightchar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import urlshortener.common.services.MetricsViewConfig;
import urlshortener.common.services.RankInfoContributor;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ClickRepositoryImpl;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.repository.ShortURLRepositoryImpl;
import urlshortener.common.services.URLAliveService;

@Configuration
@ComponentScan(basePackageClasses = { RankInfoContributor.class, MetricsViewConfig.class, URLAliveService.class})
public class PersistenceContext {

	@Autowired
    protected JdbcTemplate jdbc;

	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
 	
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
}
