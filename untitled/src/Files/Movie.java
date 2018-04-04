package Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ido on 18/03/2018.
 */
public class Movie extends MediaFile {

    // TODO: 21/03/2018 Maybe this class not so necessary

    private static final String FULL_RATINGS_KEY = "Ratings";

    private static final String GENRE_KEY = "Genre";

    private static final String PLOT_KEY = "Plot";

    private static final String SOURCE_KEY = "Source";

    private static final String VALUE_KEY = "Value";

    private static final String USER_RATING_KEY = "My Rating";

    private HashMap<String, String> ratings;

    private String genre;

    private String plotSummary;

    public Movie(JSONObject info) {
        super(info);
        try {
            JSONArray ratingsArray = info.getJSONArray(FULL_RATINGS_KEY);
            this.ratings = new HashMap<>();
            for (int i=0; i < ratingsArray.length(); i++ ){
                JSONObject currentSite = ratingsArray.getJSONObject(i);
                this.ratings.put(currentSite.getString(SOURCE_KEY), currentSite.getString(VALUE_KEY));
            }
            this.genre = info.getString(GENRE_KEY);
            this.plotSummary = info.getString(PLOT_KEY);
        } catch (JSONException e){
            this.ratings = null;
        }

    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlotSummary() {
        return plotSummary;
    }

    public HashMap<String, String> getRatings() {
        return ratings;
    }

    @Override
    public void setUserRating(String userRating) {
        super.setUserRating(userRating);
        ratings.putIfAbsent(USER_RATING_KEY, userRating);
    }
}
