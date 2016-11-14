package urlshortener.common.domain;

/**
 * Created by sergio on 14/11/16.
 */
public class RankPosition {
    private int position;
    private String hashed;
    private long score;

    public RankPosition(int position, String hashed, long score){
        this.position=position;
        this.hashed=hashed;
        this.score=score;
    }

    public int getPosition() {
        return position;
    }

    public String getHashed() {
        return hashed;
    }

    public long getScore() {
        return score;
    }
}
