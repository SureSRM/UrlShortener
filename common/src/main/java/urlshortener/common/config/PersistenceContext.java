package urlshortener.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { urlshortener.common.repository.ClickRepository.class,
        urlshortener.common.repository.ShortURLRepository.class, urlshortener.common.admin.CustomMetrics.class})
public class PersistenceContext {

}
