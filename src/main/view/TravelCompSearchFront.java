package main.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import main.common.AppConfig;
import main.controller.TravelCompSearchController;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;

public class TravelCompSearchFront {

	private JFrame frame;
	private JComboBox cityComboBox;
	private JTextField numAvailableSeats;
	private JTextField numCarCreate;
	private static TravelCompSearchController companionController;

	public static void runApp() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TravelCompSearchFront window = new TravelCompSearchFront();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TravelCompSearchFront() {
		companionController = TravelCompSearchController.getInstance();
		initialize();
	}


	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 510, 302);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Number of available seats:");
		lblNewLabel.setBounds(30, 33, 132, 14);
		frame.getContentPane().add(lblNewLabel);
		
		numAvailableSeats = new JTextField();
		numAvailableSeats.setText("1");
		numAvailableSeats.setBounds(178, 30, 110, 20);
		frame.getContentPane().add(numAvailableSeats);
		numAvailableSeats.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Destination:");
		lblNewLabel_1.setBounds(30, 89, 132, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		initCityComboBox();
		
		JLabel lblNewLabel_2 = new JLabel("Number of cars created:");
		lblNewLabel_2.setBounds(30, 145, 132, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		numCarCreate = new JTextField();
		numCarCreate.setText("1");
		numCarCreate.setBounds(178, 142, 110, 20);
		numCarCreate.setColumns(10);
		frame.getContentPane().add(numCarCreate);
		
		getStartButton();
		
		getCreateButton();
		
		getReportButton();
		
		getDownloadButton();
		
		getSaveAndExitButton();
		
		getExitButton();
		
	}
	
	private void initCityComboBox() {
		cityComboBox = new JComboBox();
		cityComboBox.setModel(new DefaultComboBoxModel(new String[] {"Moscow", "Sochi", "Volgograd", "Piter", "Voronezh"}));
		cityComboBox.setSelectedIndex(0);
		cityComboBox.setBounds(178, 85, 110, 22);
		frame.getContentPane().add(cityComboBox);
	}
	
	private void getStartButton() {
		JButton startBtn = new JButton("Start App");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				companionController.activateFlowTravelers();
			}
		});
		startBtn.setBounds(68, 226, 132, 23);
		frame.getContentPane().add(startBtn);
	}
	
	private void getCreateButton() {
		JButton createBtn = new JButton("Create");
		createBtn.setBounds(364, 33, 110, 23);
		createBtn.addActionListener(e -> createCars());
		frame.getContentPane().add(createBtn);
	}
	
	private void getReportButton() {
		JButton reportBtn = new JButton("Get report");
		reportBtn.setBounds(364, 85, 110, 23);
		reportBtn.addActionListener(e -> openFileChooser());
		frame.getContentPane().add(reportBtn);
	}
	
	private void getDownloadButton() {
		JButton downloadBtn = new JButton("Download Old Version");
		downloadBtn.setBounds(364, 141, 110, 23);
		downloadBtn.addActionListener(e -> companionController.downloadProgramStatus());
		frame.getContentPane().add(downloadBtn);
	}
	
	private void getSaveAndExitButton() {
		JButton saveExitBtn = new JButton("Save & Exit");
		saveExitBtn.setBounds(210, 226, 132, 23);
		saveExitBtn.addActionListener(e -> {
          companionController.saveProgramStatus();
          System.exit(0);
      });
		frame.getContentPane().add(saveExitBtn);
	}
	
	private void getExitButton() {
		JButton exitBtn = new JButton("Exit");
		exitBtn.setBounds(385, 226, 89, 23);
		exitBtn.addActionListener(e -> System.exit(0));
		frame.getContentPane().add(exitBtn);
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
