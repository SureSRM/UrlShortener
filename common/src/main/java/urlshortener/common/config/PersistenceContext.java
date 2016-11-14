package urlshortener.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import urlshortener.common.infocontributors.RankInfoContributor;

@Configuration
@ComponentScan(basePackageClasses = { urlshortener.common.repository.ClickRepository.class,
        urlshortener.common.repository.ShortURLRepository.class, RankInfoContributor.class})
public class PersistenceContext {

}
