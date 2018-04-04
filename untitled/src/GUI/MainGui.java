package GUI;

import Logic.Manager;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by Ido on 19/03/2018.
 */
public class MainGui {

    public static final int RECORD_CHARTS_KEY = 1;

    public static final int WATCH_HABITS_KEY = 2;

    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JButton chooseFileButton;
    private JFileChooser fileChooser;
    private JTextField fileNameField;
    private JTextArea movieDetails;
    private JScrollPane seasonInfo;
    private JButton runMedia;
    private JScrollPane mustRecentInfo;
    private JScrollPane movieDetailsWrapper;
    private JPanel dateChooserPanel;
    private JCheckBox includeSeriesCheckBox;
    private JCheckBox includeMoviesCheckBox;
    private JPanel typeFilterPanel;
    private JButton watchHabitsButton;
    private JButton recordChartsButton;
    private JPanel statsTypePanel;
    private JDateChooser startDate;
    private JDateChooser endDate;

    private Manager manager;


    public MainGui() {
        getManager();
        initComponents();
    }

    private void initComponents() {
        initFileChooser();
        initChooseFile();
        initRunMedia();
        initRecentFiles();
        initDateChoosers();
        initStatsButtons();
    }

    private void initFileChooser(){
        this.fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Media files", "mkv", "mp4", "flv", "ts");
        fileChooser.setFileFilter(filter);
    }

    private void initChooseFile() {
        chooseFileButton.addActionListener(new ActionListener() {

            private void showInfo(){
                fileNameField.setText(manager.getChosenFileTitle());
                if (manager.isChosenFileEpisode()){
                    movieDetailsWrapper.setVisible(false);
                    seasonInfo.setViewportView(new SeasonTable(manager.getSeasonInfo()));
                    seasonInfo.setVisible(true);
                } else {
                    seasonInfo.setVisible(false);
                    movieDetails.setText(manager.getMovieInfo());
                    movieDetailsWrapper.setVisible(true);
                }
                mainPanel.repaint();
            }

            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileHidingEnabled(false);
                int returnVal = fileChooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    if (manager.findFileInfo(fileChooser.getSelectedFile().getAbsolutePath())){
                        showInfo();
                    }
                    // TODO: 19/03/2018 handle situation when file not found
                }
            }
        });
    }

    private void initRecentFiles(){
        final JTable recentFiles = new RecentFilesTable(manager.getMustRecentList());
        recentFiles.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
                    System.out.println(recentFiles.getSelectedRow());
                    manager.runFromRecentFiles(recentFiles.getSelectedRow());
                    RecentFilesTable mustRecentTable = (RecentFilesTable)mustRecentInfo.getViewport().getView();
                    mustRecentTable.setModel(new DefaultTableModel(manager.getMustRecentList(),
                            RecentFilesTable.LAST_SEEN_INFO_COLUMNS));
                    mainPanel.repaint();
                }
            }
        });
        mustRecentInfo.setViewportView(recentFiles);
        mainPanel.repaint();
    }

    private void initRunMedia(){
        runMedia.addActionListener(e -> {
            if (fileNameField.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "You should choose file first.");
                return;
            }
            if(!manager.runFromPlayBtn()){
                JOptionPane.showMessageDialog(null, "File no longer exists.");
                return;
            }
            fileNameField.setText("");
            initRecentFiles();

        });
    }

    private void initDateChoosers(){
        dateChooserPanel.setLayout(new BorderLayout());
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        TitledBorder startDateBorder = BorderFactory.createTitledBorder(blackLine, "תאריך התחלה");
        startDateBorder.setTitleJustification(TitledBorder.RIGHT);
        TitledBorder endDateBorder = BorderFactory.createTitledBorder(blackLine, "תאריך סיום");
        endDateBorder.setTitleJustification(TitledBorder.RIGHT);
        JPanel startDateWrapper = new JPanel(new BorderLayout());
        startDate = new JDateChooser();
        startDateWrapper.setBorder(startDateBorder);
        startDateWrapper.add(startDate, BorderLayout.CENTER);
        JPanel endDateWrapper = new JPanel(new BorderLayout());
        endDate = new JDateChooser();
        endDateWrapper.setBorder(endDateBorder);
        endDateWrapper.add(endDate, BorderLayout.CENTER);
        dateChooserPanel.add(startDateWrapper, BorderLayout.NORTH);
        dateChooserPanel.add(endDateWrapper, BorderLayout.SOUTH);
    }

    public void initStatsButtons(){
        watchHabitsButton.addActionListener(e -> manager.getStatsGraph(WATCH_HABITS_KEY, startDate.getDate(), endDate.getDate(),
                includeMoviesCheckBox.isEnabled(), includeSeriesCheckBox.isEnabled()));
        recordChartsButton.addActionListener(e -> manager.getStatsGraph(RECORD_CHARTS_KEY, startDate.getDate(), endDate.getDate(),
                includeMoviesCheckBox.isEnabled(), includeSeriesCheckBox.isEnabled()));
    }

    public void onClose(){
        try {
            // Write to disk with FileOutputStream
            FileOutputStream f_out = new FileOutputStream("manager.ser");

            // Write object with ObjectOutputStream
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            // Write object out to disk
            obj_out.writeObject(manager);
            obj_out.close();
            f_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getManager(){
        try {
            FileInputStream fileIn = new FileInputStream("manager.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            manager = (Manager) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            manager = new Manager();
        } catch (ClassNotFoundException c) {
            System.out.println("Manager class not found");
            c.printStackTrace();
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("MainGui");
        final MainGui runningGUI = new MainGui();
        frame.setContentPane(runningGUI.mainPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();

                int result = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit the application?",
                        "Exit Application", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    runningGUI.onClose();
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

}
