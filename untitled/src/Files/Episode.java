package Files;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ido on 18/03/2018.
 */
public class Episode extends MediaFile {

    private static final String SEASON_NUMBER_KEY = "Season";

    private static final String EPISODE_NUMBER_KEY = "Episode";

    private int seasonNumber;

    private int episodeNumber;

    private String seriesTitle;

    public Episode(JSONObject info, String seriesTitle) {
        super(info);
        this.seriesTitle = seriesTitle;
        try {
            this.seasonNumber = Integer.parseInt(info.getString(SEASON_NUMBER_KEY));
            this.episodeNumber = Integer.parseInt(info.getString(EPISODE_NUMBER_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return String.format("%s - %s (S%dE%d)", seriesTitle, title, seasonNumber,
                episodeNumber) ;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }
}
