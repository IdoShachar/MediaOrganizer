package Logic; /**
 * Created by Ido on 17/03/2018.
 */

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileParser {

    private static final Pattern seriesPattern = Pattern.compile("(s\\+?[0-9]?[0-9]\\+?e\\+?[0-9]?[0-9])" +
            "|(season\\+?[0-9]?[0-9]\\+?episode\\+?[0-9]?[0-9])|([0-9]?[0-9]\\+?of\\+?[0-9]?[0-9])|(EP?\\+?[0-9]?[0-9])|" +
            "(PART\\+?[0-9]?[0-9])", Pattern.CASE_INSENSITIVE);

    private static final Pattern movieYearOrResPattern = Pattern.compile("19[4-9][0-9]|20[0-3][0-9]" +
            "|720p|1080[p|i]|HDTV|Blu-?ray|[4|8]K|UHDTV|DVD", Pattern.CASE_INSENSITIVE);

    private static final String producingNameRegex = "(?i)marvels?|bbc";

    private static FileParser instance = new FileParser();

    private HashMap<String, String> brokenTitles = new HashMap<>();

    private String filePath;

    private String title;

    private int seasonNumber;

    private int episodeNumber;

    private String fileName;

    private void init(){
        this.title = "";
        this.seasonNumber = -1;
        this.episodeNumber = -1;
    }

    private FileParser() {
        init();
    }

    public static FileParser getInstance() {
        return instance;
    }

    private String parseSeries(Matcher seasonAndEpisodeMatcher){
        this.title = fileName.substring(0, seasonAndEpisodeMatcher.start()-1);
        if (brokenTitles.containsKey(this.title)){
            this.title = brokenTitles.get(this.title);
        }
        else if (Pattern.matches(producingNameRegex, this.title.split("\\+",2)[0])){
            this.title = this.title.split("\\+",2)[1];
        }
        System.out.println("Series Name: " + this.title);
        String[] match = seasonAndEpisodeMatcher.group(0).split("\\D+");
        int firstNumberIndex = (match[0].equals("")) ? 1 : 0;
        if (seasonAndEpisodeMatcher.group(0).toLowerCase().startsWith("s")) {
            this.seasonNumber = Integer.parseInt(match[firstNumberIndex]);
            this.episodeNumber = Integer.parseInt(match[firstNumberIndex+1]);
        } else {
            // only the number of the episode, assume first season
            this.seasonNumber = 1;
            this.episodeNumber = Integer.parseInt(match[firstNumberIndex]);
        }
        System.out.println(String.format("Season: %s\nEpisode: %s", this.seasonNumber, this.episodeNumber));
        return this.title + "&Season=" + this.seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isEpisode(){
        return episodeNumber != -1;
    }

    private String parseMovie(){
        Matcher yearOrResMatcher = movieYearOrResPattern.matcher(fileName);
        if (yearOrResMatcher.find()){
            this.title = fileName.substring(0, yearOrResMatcher.start()-1);
            if (brokenTitles.containsKey(this.title)){
                this.title = brokenTitles.get(this.title);
            }
            return this.title;
        }
        return "";
    }

    public String parseFileName(String filePath){
        init();
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf('\\')+1).replaceAll("[ |.]","+");
        String parameters = "&t=";
        Matcher seriesMatcher = seriesPattern.matcher(fileName);
        if (seriesMatcher.find()){
            parameters += parseSeries(seriesMatcher);
        } else {
            parameters += parseMovie();
            System.out.println(this.title);
        }
        return parameters;
    }

}
