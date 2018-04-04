package GUI;

import Files.MediaFile;
import Files.Movie;
import Logic.Manager;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.*;

public class FileReviewDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel fileName;
    private JCheckBox stoppedCheckBox;
    private JTextField stoppedTimeField;
    private JTextArea userReviewArea;
    private JTextField userRatingField;
    private JTextField fileTitleField;
    private JPanel okAndCancelPanel;
    private JPanel Info;

    private Manager manager;

    private MediaFile runningFile;

    public FileReviewDialog(Manager manager, MediaFile runningFile) {
        this.manager = manager;
        this.runningFile = runningFile;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        stoppedCheckBox.addActionListener(e -> stoppedTimeField.setEnabled(!stoppedTimeField.isEnabled()));

        fileTitleField.setText(manager.getChosenFileTitle());

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        String stopMinute = (stoppedTimeField.isEnabled()) ? stoppedTimeField.getText() : "";
        manager.setFileInfo(runningFile, stopMinute, userRatingField.getText(), userReviewArea.getText());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        FileReviewDialog dialog = new FileReviewDialog(new Manager(), new Movie(new JSONObject()));
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
