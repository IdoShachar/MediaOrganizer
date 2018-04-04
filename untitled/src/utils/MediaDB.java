package utils; /**
 * Created by Ido on 17/03/2018.
 */

import Files.Episode;
import Files.MediaFile;
import Files.MediaFileComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class MediaDB implements Serializable{

    private static final int MAX_SIZE = 8;

    private static final String FILTERED_TABLE_PATH = System.getProperty("user.dir")
            + "\\filtered_db.csv";

    private static final String FILTERED_TABLE_HEADER = "Title,Last Seen,User Rating,IMDB " +
            "Rating,Fully Watched,Is Episode\n";

    private HashMap<String, MediaFile> DB;

    private HashMap<String, HashMap<Integer, HashSet<Integer>>> watchedEpisodes;

    private PriorityQueue<MediaFile> mustRecent;

    private MediaFile lastOpenedFile;

    public MediaDB() {
        this.DB = new HashMap<>();
        this.watchedEpisodes = new HashMap<>();
        this.mustRecent = new PriorityQueue<>(MAX_SIZE, new MediaFileComparator());
    }

    public boolean contains(String id){
        return DB.containsKey(id);
    }

    public MediaFile getMediaFile(String id){
        return DB.get(id);
    }

    public void markAsWatched(MediaFile file){
        file.markAsWatched();
        if (file.isEpisode()){
            addWatchedEpisode((Episode) file);
        }
    }

    public boolean addMediaFile(MediaFile newFile){
        MediaFile file;
        file = DB.putIfAbsent(newFile.getID(), newFile);
        if (file != null){
            this.lastOpenedFile = file;
        } else {
            this.lastOpenedFile = newFile;
        }
        this.lastOpenedFile.updateDate();
        return true;
    }

    public void addMustRecent(){
        mustRecent.remove(lastOpenedFile);
        mustRecent.add(lastOpenedFile);
        if (mustRecent.size() > MAX_SIZE){
            mustRecent.poll();
        }
    }

    public MediaFile[] getMustRecentList(){
        return mustRecent.toArray(new MediaFile[mustRecent.size()]);
    }

    private void addWatchedEpisode(Episode lastSeenEpisode){
        String seriesTitle = lastSeenEpisode.getSeriesTitle();
        int seasonNumber = lastSeenEpisode.getSeasonNumber();
        int episodeNumber = lastSeenEpisode.getEpisodeNumber();
        if (!watchedEpisodes.containsKey(seriesTitle)){
            watchedEpisodes.put(seriesTitle, new HashMap<>());
        }
        if (!watchedEpisodes.get(seriesTitle).containsKey(seasonNumber)){
            watchedEpisodes.get(seriesTitle).put(seasonNumber, new HashSet<>());
        }
        watchedEpisodes.get(seriesTitle).get(seasonNumber).add(episodeNumber);
    }

    public Integer[] getWatchedEpisodes(String seriesTitle, int seasonNumber){
        if (watchedEpisodes.containsKey(seriesTitle) && watchedEpisodes.get(seriesTitle)
                .containsKey(seasonNumber)){
            HashSet<Integer> episodes = watchedEpisodes.get(seriesTitle).get(seasonNumber);
            return episodes.toArray(new Integer[episodes.size()]);
        }
        return new Integer[0];
    }

    public void addUserReview(String review){
        this.lastOpenedFile.setUserReview(review);
    }

    public void addUserRating(String userRating) { this.lastOpenedFile.setUserRating(userRating);}

    public void setStopMinute(String stopMinute) { this.lastOpenedFile.setStopMinute(Integer
            .parseInt(stopMinute));}

    public String saveFilteredDB(final Date startDate, final Date endDate, final boolean includeMovies, final boolean includeSeries) {
        Map<String, MediaFile> filteredDB = DB.entrySet().stream()
                .filter(file -> checkFile(file.getValue(), startDate, endDate, includeMovies, includeSeries))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        printToFile(filteredDB);
        return FILTERED_TABLE_PATH;
    }

    private void printToFile(Map<String, MediaFile> filteredDB) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(FILTERED_TABLE_PATH));
            StringBuilder sb = new StringBuilder();
            sb.append(FILTERED_TABLE_HEADER);
            for (Map.Entry<String, MediaFile> entry : filteredDB.entrySet())
            {
                sb.append(entry.getValue().getShortData());
            }
            pw.print(sb.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean checkFile(MediaFile file, Date startDate, Date endDate, boolean
            includeMovies, boolean includeSeries){
        if ((startDate != null && file.getLastSeen().compareTo(startDate) < 0) ||
                (endDate != null && file.getLastSeen().compareTo(endDate) > 0 )){
            return false;
        }
        if (file.isEpisode() && !includeSeries){
            return false;
        }
        if (!file.isEpisode() && !includeMovies){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MediaDB{" +
                "DB=" + DB +
                '}';
    }

    // TODO: 04/04/2018 for debugging purpose only - remove later
    public void printReviews(){
        for (Map.Entry<String, MediaFile> entry : DB.entrySet())
        {
            System.out.println(entry.getValue().getTitle() + ": " + entry.getValue().getUserReview());
        }
    }
}
