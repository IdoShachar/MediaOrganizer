package Logic;

import Files.Episode;
import Files.MediaFile;
import Files.Movie;
import GUI.FileReviewDialog;
import GUI.MainGui;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.MediaDB;
import utils.RunnableBSPlayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ido on 19/03/2018.
 */
public class Manager implements Serializable {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537" +
            ".36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";

    private static final String API_KEY = "109d2c97";

    private static final String DATA_REQUEST_URL = String.format("http://www.omdbapi.com/?apikey=%s",
            API_KEY);

    private static final String RESPONSE_KEY = "Response";

    private static final String EPISODES_KEY = "Episodes";

    private static final String TITLE_KEY = "Title";

    private static final String ID_KEY = "imdbID";

    private static final String IMDB_RATING_KEY = "imdbRating";

    private MediaDB DB;

    private transient FileParser fileParser = FileParser.getInstance();

    private transient JSONObject lastChooseFileDescriptor;

    private transient MediaFile lastChosenFile;

    public Manager() {
        this.DB = new MediaDB();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        fileParser = FileParser.getInstance();
    }

    private void updateChosenFile(){
        if (fileParser.isEpisode()){
            try {
                JSONObject episodeInfo = lastChooseFileDescriptor.getJSONArray
                        (EPISODES_KEY).getJSONObject(fileParser.getEpisodeNumber()-1);
                // -1 because of zero indexing
                String id = episodeInfo.getString(ID_KEY);
                if (!DB.contains(id)){
                    lastChosenFile = new Episode(requestFileData("&i=" + id),
                            lastChooseFileDescriptor.getString(TITLE_KEY));
                } else {
                    lastChosenFile = DB.getMediaFile(id);
                }
            } catch (Exception e){
                lastChosenFile = null;
            }
        } else {
            try {
                String id = lastChooseFileDescriptor.getString(ID_KEY);
                lastChosenFile = (DB.contains(id)) ? (Movie) DB.getMediaFile(id) : new Movie
                        (lastChooseFileDescriptor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        lastChosenFile.setFilePath(fileParser.getFilePath());
    }

    public void updateFileInfo(MediaFile runningFile){
        // TODO: 01/04/2018 might be a problem when choosing another file
        System.out.println("Current running file " + runningFile.getTitle());
        FileReviewDialog reviewInfo = new FileReviewDialog(this, runningFile);
        reviewInfo.setTitle(runningFile.getTitle() + "Review");
        reviewInfo.pack();
        reviewInfo.setVisible(true);
    }

    public void setFileInfo(MediaFile runningFile, String stopMinute, String userRating, String
            userReview){
        if (stopMinute.isEmpty()){
            this.DB.markAsWatched(runningFile);
        } else {
            runningFile.setStopMinute(Integer.parseInt(stopMinute));
        }
        if (!userReview.isEmpty()){
            runningFile.setUserReview(userReview);
        }
        if (!userRating.isEmpty()){
            runningFile.setUserRating(userRating);
        }
    }

    public boolean runFromRecentFiles(int fileIndex){
        lastChosenFile = DB.getMustRecentList()[fileIndex];
        return runFromPlayBtn();
    }

    public boolean runFromPlayBtn(){
        String filePath = lastChosenFile.getFilePath();
        if (!new File(filePath).exists()){
            return false;
        }
        Thread playerThread = new Thread(new RunnableBSPlayer(this));
        playerThread.start();
        this.DB.addMediaFile(lastChosenFile);
        this.DB.addMustRecent();
        return true;
    }

    private JSONObject requestFileData(String parameters) {

        String searchURL = DATA_REQUEST_URL + parameters;

        JSONObject fileDetails = null;
        HttpURLConnection con = null;

        try {
            URL obj = new URL(searchURL);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            // TODO: 18/03/2018 check response code
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            fileDetails = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Can't connect to the Internet");
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return fileDetails;
    }

    public boolean successfulFind(){
        return lastChooseFileDescriptor.length() != 2;  // 2 = RESPONSE + ERROR
    }

    public boolean findFileInfo(String filePath){
        lastChooseFileDescriptor = requestFileData(fileParser.parseFileName(filePath));
        if (successfulFind()){
            updateChosenFile();
            return true;
        }
        return false;
    }

    public MediaFile getLastChosenFile() {
        return lastChosenFile;
    }

    public String getChosenFileTitle(){
        if (lastChosenFile == null){
            return "";
        }
        return lastChosenFile.getTitle();
    }

    public boolean isChosenFileEpisode(){
        return lastChosenFile.isEpisode();
    }

    public String getMovieInfo(){
        if (lastChosenFile == null){
            return "";
        }
        Movie chosenMovie = (Movie) lastChosenFile;
        return String.format("Plot Summary:\n%s\nGenre:\n%s\nRatings:\n%s\nMy Review:\n%s",
                chosenMovie.getPlotSummary(), chosenMovie.getGenre(), chosenMovie.getRatings(),
                chosenMovie.getUserReview());
    }

    public Object[][] getSeasonInfo(){
        Episode chosenEpisode = (Episode) lastChosenFile;
        try {
            JSONArray season = lastChooseFileDescriptor.getJSONArray(EPISODES_KEY);
            Object[][] seasonInfo = new Object[season.length()][4];
            for (int i = 0; i < season.length(); i++){
                JSONObject currentEpisode = season.getJSONObject(i);
                seasonInfo[i] = new Object[]{ currentEpisode.getString(TITLE_KEY), currentEpisode
                        .getString(IMDB_RATING_KEY), null, false};
            }
            Integer[] watchedEpisodesNumbers = DB.getWatchedEpisodes(chosenEpisode.getSeriesTitle(),
                    chosenEpisode.getSeasonNumber());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            for (int epNumber: watchedEpisodesNumbers){
                MediaFile ep = DB.getMediaFile(season.getJSONObject(epNumber).getString(ID_KEY));
                seasonInfo[epNumber-1][2] = df.format(ep.getLastSeen());
                seasonInfo[epNumber-1][3] = true;
            }
            return seasonInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object[][] getMustRecentList(){
        MediaFile[] recentFiles = DB.getMustRecentList();
        Object[][] recentFilesInfo = new Object[recentFiles.length][2];
        for (int i = 0; i < recentFiles.length; i++){
            recentFilesInfo[i] = new Object[]{recentFiles[i].getTitle(), recentFiles[i].isFullyWatched()};
        }
        return recentFilesInfo;
    }

    public void getStatsGraph(int statsTypeKey, Date startDate, Date endDate, 
                              boolean includeMovies, boolean includeSeries) {
        String DBpath = DB.saveFilteredDB(startDate, endDate, includeMovies, includeSeries);
        if (statsTypeKey == MainGui.WATCH_HABITS_KEY){
            // TODO: 03/04/2018 run R script for watch habits 
        } else {
            // TODO: 03/04/2018 run R script for records charts 
        }
    }

    // function only for testing purpose
    public void addMediaFile(){
        this.DB.addMediaFile(lastChosenFile);
    }

    public void markLastSeenAsWatched(){
        this.DB.markAsWatched(lastChosenFile);
    }

    public void addReview(String review){
        this.DB.addUserReview(review);
    }

    public static void testFileNameParsing(String testFilePath) throws Exception{
        BufferedReader in = new BufferedReader(
                new FileReader(testFilePath));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            System.out.println(FileParser.getInstance().parseFileName(inputLine));
        }
        in.close();
    }

    public static void testDB_Add_Pull_Watched_Reviews(String testFilePath) throws Exception{
        Manager manager = new Manager();
        BufferedReader in = new BufferedReader(
                new FileReader(testFilePath));
        String inputLine;
        Random rand = new Random();

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            if (manager.findFileInfo(inputLine)){
                manager.addMediaFile();
                if (rand.nextBoolean()){
                    manager.addReview(UUID.randomUUID().toString());
                }
                if (rand.nextBoolean()){
                    manager.markLastSeenAsWatched();
                }
            }
        }
        in.close();
        System.out.println(manager.DB);
        manager.DB.printReviews();
    }

    public static void testLogicFunctioning() throws Exception{
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter path of test file:");
        String testFilePath = reader.next();
        System.out.println("Enter Test number:\n" +
                "1-Parse file name\n" +
                "2-Add and pull from DB");
        int n = reader.nextInt();
        reader.close();
        switch (n){
            case 1: {
                System.out.println("Testing 1 - Parse file name");
                testFileNameParsing(testFilePath);
            }
            case 2:{
                System.out.println("Testing 2 - Add and pull from DB");
                testDB_Add_Pull_Watched_Reviews(testFilePath);
            }
        }
    }

    public static void testGuiFunctioning() throws Exception{
        MainGui gui = new MainGui();

    }

    public static void main(String[] args) throws Exception {
//        Logic.Manager manager = new Logic.Manager();
//        PriorityQueue<Files.MediaFile> test = new PriorityQueue<Files.MediaFile>(8, new Comparator<Files.MediaFile>() {
//            @Override
//            public int compare(Files.MediaFile o1, Files.MediaFile o2) {
//                return o1.getLastSeen().compareTo(o2.getLastSeen());
//            }
//        });
//        BufferedReader in = new BufferedReader(
//                new FileReader("C:\\Users\\Ido\\IdeaProjects\\MediaOrganizer\\filePaths"));
//        String inputLine;
//        Random rand = new Random();
//
//        while ((inputLine = in.readLine()) != null) {
//            System.out.println(inputLine);
//            if (manager.findFileInfo(inputLine)){
//                manager.lastChosenFile.updateDate();
//                test.add(manager.lastChosenFile);
//            }
//        }
//        in.close();


        //testLogicFunctioning();
        //testGuiFunctioning();

    }
}
