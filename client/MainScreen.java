package com.g10.portfolio1.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainScreen extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen frame = new MainScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainScreen() {
		//Set Frame and main panel to contentPane
		setTitle("Grade Tracker | Main");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getCourseSelectorPanel());
		
		contentPane.add(mainPanel, BorderLayout.CENTER);
		
	}

	private JPanel getCourseSelectorPanel() {
		
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));

		JLabel semLabel = new JLabel("Semester:    ");
		JComboBox<String> semesterCB = new JComboBox<>();
		JLabel courseLabel = new JLabel("     Course:     ");
		JComboBox<String> courseCB = new JComboBox<>();
		courseCB.setEnabled(false);
		
		//Call Server for combo box lists
		ArrayList<String> semesterList = new ArrayList<>();
		//semesterList = requestSemesterList();
		
		for (String element : semesterList)
			semesterCB.addItem(element);
		
		semesterCB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				//Request course list from selected semester
				ArrayList<String> courseList = new ArrayList<>();
				
				//courseList = requestCourseList(semesterCB.getSelectedItem());
				
				for (String element : courseList)
					courseCB.addItem(element);
				
				courseCB.setEnabled(true);
			}
		});
		
		selectorPanel.add(semLabel);
		selectorPanel.add(semesterCB);
		selectorPanel.add(courseLabel);
		selectorPanel.add(courseCB);
		
		selectorPanel.setMaximumSize(new Dimension(selectorPanel.getMaximumSize().width, 30));
		
		return selectorPanel;
	}

}
