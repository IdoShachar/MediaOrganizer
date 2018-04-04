package Files; /**
 * Created by Ido on 17/03/2018.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public abstract class MediaFile implements Serializable{

    private static final String ID_KEY = "imdbID";

    private static final String IMDB_RATING_KEY = "imdbRating";

    private static final String TITLE_KEY = "Title";

    private static final String TYPE_KEY = "Type";

    private String filePath;

    private Date lastSeen;

    private String ID;

    private String userReview;

    private String userBrief;

    private String imdbRating;

    private String userRating;

    private boolean fullyWatched;

    private boolean isEpisode;

    private int stopMinute;

    protected String title;

    public MediaFile(JSONObject info){
        this.fullyWatched = false;
        this.stopMinute = 0;
        this.userRating = "";
        this.userBrief = "";
        this.filePath = "";
        try {
            this.ID = info.getString(ID_KEY);
            this.title = info.getString(TITLE_KEY);
            this.imdbRating = info.getString(IMDB_RATING_KEY);
            this.isEpisode = info.getString(TYPE_KEY).equals("episode");
        } catch (JSONException e){
            this.ID = "";
            this.title = "";
            this.imdbRating = "";
            this.isEpisode = false;
        }
    }

    public void updateDate() {
        this.lastSeen = new Date();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void markAsWatched(){
        this.fullyWatched = true;
        this.stopMinute = Integer.MAX_VALUE;
    }

    public boolean isFullyWatched(){
        return fullyWatched;
    }

    public String getID() {
        return ID;
    }

    public int getStopMinute() {
        return stopMinute;
    }

    public void setStopMinute(int stopMinute) {
        this.stopMinute = stopMinute;
    }

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getUserBrief() {
        return userBrief;
    }

    public void setUserBrief(String userBrief) {
        this.userBrief = userBrief;
    }

    public boolean isEpisode() {
        return isEpisode;
    }

    public abstract String getTitle();

    @Override
    public String toString(){
        return getTitle();
    }

    public String getShortData(){
        return String.format("%s,%s,%s,%s,%s,%s\n", getTitle(),(lastSeen == null) ? "-" :
                lastSeen.toString(), userRating, imdbRating, fullyWatched, isEpisode);
    }
}
