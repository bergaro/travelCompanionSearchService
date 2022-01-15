package main.view;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.Objects;
import main.common.AppConfig;
import main.controller.TravelCompSearchController;

public class TravelCompSearchFront {

    private JButton exitBtn;
    private JButton saveBtn;
    private JButton reportBtn;
    private JButton createCar;
    private JButton testDesBtn;
    private JPanel mainWindow;
    private JComboBox cityComboBox;
    private JTextField numCarCreate;
    private JTextField numAvailableSeats;
    private static TravelCompSearchController companionController;

    public TravelCompSearchFront() {
        exitBtn.addActionListener(event -> System.exit(0));
        createCar.addActionListener(e -> createCars());
        reportBtn.addActionListener(e -> openFileChooser());
        saveBtn.addActionListener(e -> {
            companionController.saveProgramStatus();
            System.exit(0);
        });
        testDesBtn.addActionListener(e -> companionController.downloadProgramStatus());
    }

    public static void runApp() {
        companionController = TravelCompSearchController.getInstance();
        viewWindow();
        companionController.activateFlowTravelers();
    }

    private static void viewWindow() {
        JFrame frame = new JFrame(AppConfig.getWindowName());
        frame.setContentPane(new TravelCompSearchFront().mainWindow);
        frame.setPreferredSize(new Dimension(AppConfig.getWindowWidth(), AppConfig.getWindowHeight()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createCars() {
        int carNumSeats = Integer.parseInt(numAvailableSeats.getText());
        String matchingPoint = Objects.requireNonNull(cityComboBox.getSelectedItem()).toString();
        int numCarForCreate = Integer.parseInt(numCarCreate.getText());
        companionController.addCarToPark(matchingPoint, carNumSeats, numCarForCreate);
    }

    private void openFileChooser() {
        final String defaultFileName = AppConfig.getReportFileName();
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultFileName));
        int select = fileChooser.showSaveDialog(parentFrame);
        if (select == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            companionController.saveReport(file);
        }
    }



}
