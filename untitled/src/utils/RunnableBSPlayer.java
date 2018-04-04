package utils;

import Files.MediaFile;
import Logic.Manager;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Ido on 01/04/2018.
 */
public class RunnableBSPlayer implements Runnable {

    private static final String BSPLAYER_PATH = "C:\\Program Files (x86)\\Webteh\\BSPlayer\\";

    private static final String CONTINUE_MSG = "Do you want to continue from where you left off?";

    private static final String START_AT_FLAG = "-stime=";

    private Manager manager;

    public RunnableBSPlayer(Manager manager) {
        this.manager = manager;
    }

    private int getStartingSecond(MediaFile runningFile){
        int startAt = 0;
        if (!runningFile.isFullyWatched() && runningFile.getStopMinute() > 0){
            int startFromLastEnded = JOptionPane.showConfirmDialog(null, CONTINUE_MSG, "Starting point",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (startFromLastEnded == JOptionPane.OK_OPTION){
                startAt = runningFile.getStopMinute()*60;  // start at x seconds
            }
        }
        return startAt;
    }
    @Override
    public void run() {

        MediaFile runningFile = manager.getLastChosenFile();
        String[] cmdarray = {"\"" + BSPLAYER_PATH + "bsplayer.exe\"", "\"" + runningFile
                .getFilePath() + "\"", START_AT_FLAG + getStartingSecond(runningFile)};
        try {
            Process process = Runtime.getRuntime().exec(cmdarray);
            System.out.println("Start running thread");
            process.waitFor();
            System.out.println("End running thread");
            manager.updateFileInfo(runningFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
