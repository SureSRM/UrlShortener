package urlshortener.common.infocontributors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import urlshortener.common.domain.RankPosition;
import urlshortener.common.repository.ClickRepository;

import java.util.List;

@Service
public class RankInfoContributor implements InfoContributor{

    @Autowired
    protected ClickRepository clickRepository;

    List<RankPosition> rankPositionList;

    public RankInfoContributor() {
        //this.rankPositionList = clickRepository.listTop(10);
    }

    @Scheduled(fixedRate = 500)
    public void updateRank() {
        rankPositionList=clickRepository.listTop(10);
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("RankList", rankPositionList);
    }

}
