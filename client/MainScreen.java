package com.g10.portfolio1.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainScreen extends JFrame {

	private JPanel contentPane;
	private JComboBox<String> semesterCB;
	private JComboBox<String> courseCB;
	private DefaultComboBoxModel<String> semCBModel;
	private DefaultComboBoxModel<String> courseCBModel;
	

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
		// Set Frame and main panel to contentPane
		setTitle("Grade Tracker | Main");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(getCoursePanel());

		contentPane.add(mainPanel, BorderLayout.CENTER);

	}

	private JPanel getCoursePanel() {

		JPanel coursePanel = new JPanel();
		coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));

		coursePanel.add(getCourseSelectorPanel());
		coursePanel.add(getSemAndCourseButtons());

		return coursePanel;
	}

	private JPanel getSemAndCourseButtons() {

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());

		JButton addSemesterButton = new JButton("Add semester");
		JButton remSemesterButton = new JButton("Remove semester");
		JButton addCourseButton = new JButton("Add course");
		JButton remCourseButton = new JButton("Remove course");

		// Add action listeners
		//Set add semester button functionality
		addSemesterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String response = JOptionPane.showInputDialog(null, "What is the new semester?", "Enter new semester",
						JOptionPane.QUESTION_MESSAGE);
				if ((response != null) && !(response.equals(""))) {
					semCBModel.addElement(response);
					courseCB.setEnabled(true);
				}
			}
		});

		//Set remove semester button functionality
		remSemesterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (semCBModel.getSize() != 0) {
					semCBModel.removeElementAt(semesterCB.getSelectedIndex());
					courseCBModel.removeAllElements();
					if (semCBModel.getSize() == 0)
						courseCB.setEnabled(false);
				}
			}
		});

		addCourseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(courseCB.isEnabled()) {
					String response = JOptionPane.showInputDialog(null, "What is the new course?", "Enter new course",
							JOptionPane.QUESTION_MESSAGE);
					if ((response != null) && !(response.equals("")))
						courseCBModel.addElement(response);
				}
			}
		});
		
		//Set remove semester button functionality
		remCourseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (courseCBModel.getSize() != 0) {
					courseCBModel.removeElementAt(courseCB.getSelectedIndex());
				}
			}
		});
		
		// Add to button panel
		buttonsPanel.add(addSemesterButton);
		buttonsPanel.add(remSemesterButton);
		buttonsPanel.add(addCourseButton);
		buttonsPanel.add(remCourseButton);

		return buttonsPanel;
	}

	private JPanel getCourseSelectorPanel() {

		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));
		
		//Call server for info
		semCBModel = new DefaultComboBoxModel<>();
		courseCBModel = new DefaultComboBoxModel<>();

		JLabel semLabel = new JLabel("Semester:    ");
		semesterCB = new JComboBox<>(semCBModel);
		JLabel courseLabel = new JLabel("     Course:     ");
		courseCB = new JComboBox<>(courseCBModel);
		courseCB.setEnabled(false);

		// Call Server for combo box lists
		ArrayList<String> semesterList = new ArrayList<>();
		// semesterList = requestSemesterList();

		for (String element : semesterList)
			semesterCB.addItem(element);

//		semesterCB.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				// Request course list from selected semester
//				ArrayList<String> courseList = new ArrayList<>();
//
//				// courseList = requestCourseList(semesterCB.getSelectedItem());
//
//				for (String element : courseList)
//					courseCB.addItem(element);
//
//				courseCB.setEnabled(true);
//			}
//		});

		selectorPanel.add(semLabel);
		selectorPanel.add(semesterCB);
		selectorPanel.add(courseLabel);
		selectorPanel.add(courseCB);

		selectorPanel.setMaximumSize(new Dimension(selectorPanel.getMaximumSize().width, 30));

		return selectorPanel;
	}

}
