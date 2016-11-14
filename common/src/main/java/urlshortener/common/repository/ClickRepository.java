package urlshortener.common.repository;

import java.util.List;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.RankPosition;

public interface ClickRepository {

	List<Click> findByHash(String hash);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<RankPosition> listTop(int top);

	List<Click> list(Long limit, Long offset);
}
