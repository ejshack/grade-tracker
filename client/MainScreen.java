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
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MainScreen extends JFrame {

	private static final long serialVersionUID = 7858149595584813037L;
	private JPanel contentPane;
	private JComboBox<String> semesterCB;
	private JComboBox<String> courseCB;
	private DefaultComboBoxModel<String> semCBModel;
	private DefaultComboBoxModel<String> courseCBModel;
	private JTable assignTable;
	private DefaultTableModel tableModel;
	
	//Key - semester, Value - list of courses
	private HashMap<String, ArrayList<String>> hmSemCourses;

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
		mainPanel.add(getAssignmentPanel());

		contentPane.add(mainPanel, BorderLayout.CENTER);

	}

	private JPanel getAssignmentPanel() {
		
		JPanel assignPanel = new JPanel();
		assignPanel.setLayout(new BorderLayout());		
		assignPanel.add(new JLabel("<html><p>Assignments:</p></html>"), BorderLayout.PAGE_START);
		
		tableModel = createTableModel();
		assignTable = new JTable(tableModel);
		
		JScrollPane tableScroll = new JScrollPane(assignTable);
		
		assignPanel.add(tableScroll);
		
		assignPanel.add(getAssignmentButtonsPanel(), BorderLayout.PAGE_END);
		assignTable.setEnabled(false);
		
		return assignPanel;
	}

	private JPanel getAssignmentButtonsPanel() {
		
		JPanel assignBP = new JPanel();
		assignBP.setLayout(new FlowLayout());
		
		JButton addAssign = new JButton("Add assignment");
		JButton editAssign = new JButton("Edit assignment");
		JButton remAssign = new JButton("Remove assignment");
		
		//Add listeners for each button
		
		assignBP.add(addAssign);
		assignBP.add(editAssign);
		assignBP.add(remAssign);
		
		return assignBP;
	}

	private DefaultTableModel createTableModel() {
		DefaultTableModel tm;
		String[] colHeaders = {"Title", "Category", "Date",  "Points Earned", "Points Possible"};		
		tm = new DefaultTableModel(colHeaders, 10);
		
		return tm;
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
					hmSemCourses.put(response, new ArrayList<>());
					semCBModel.addElement(response);
					courseCB.setEnabled(true);
					semCBModel.setSelectedItem(response);
				}
			}
		});

		//Set remove semester button functionality
		remSemesterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (semCBModel.getSize() != 0) {
					semCBModel.removeElementAt(semesterCB.getSelectedIndex());
					
					hmSemCourses.remove(semesterCB.getSelectedItem());
					
					if (semCBModel.getSize() == 0) {
						courseCBModel = new DefaultComboBoxModel<>();
						courseCB.setModel(courseCBModel);
						courseCB.setEnabled(false);
						assignTable.clearSelection();
						assignTable.setEnabled(false);
					}
				}
			}
		});

		addCourseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(courseCB.isEnabled()) {
					String response = JOptionPane.showInputDialog(null, "What is the new course?", "Enter new course",
							JOptionPane.QUESTION_MESSAGE);
					if ((response != null) && !(response.equals(""))) {
						hmSemCourses.get(semCBModel.getSelectedItem()).add(response);
						courseCBModel.addElement(response);
						courseCB.setModel(courseCBModel);
						courseCBModel.setSelectedItem(response);
						assignTable.setEnabled(true);
					}
				}
			}
		});
		
		//Set remove semester button functionality
		remCourseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (courseCBModel.getSize() != 0) {
					hmSemCourses.get(semesterCB.getSelectedItem()).remove(semesterCB.getSelectedIndex());
					courseCBModel.removeElementAt(courseCB.getSelectedIndex());
				} else {
					assignTable.clearSelection();
					assignTable.setEnabled(false);
				}
			}
		});
		
		getSemAndCourses();
		
		// Add to button panel
		buttonsPanel.add(addSemesterButton);
		buttonsPanel.add(remSemesterButton);
		buttonsPanel.add(addCourseButton);
		buttonsPanel.add(remCourseButton);

		return buttonsPanel;
	}

	private void getSemAndCourses() {
		hmSemCourses = new HashMap<>();
		
		//Get semester and course information from server
		//TODO server calls
		
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
			semCBModel.addElement(element);

		semesterCB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				String semester = (String) semesterCB.getSelectedItem();
				if(semester != null) {
					ArrayList<String> courses = hmSemCourses.get(semester);
					courseCBModel = new DefaultComboBoxModel<>(courses.toArray(new String[courses.size()]));
				} else {
					courseCBModel = new DefaultComboBoxModel<>();
					courseCB.setEnabled(false);
				}
				courseCB.setModel(courseCBModel);
			}
		});
		
		courseCB.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				String course = (String) courseCB.getSelectedItem();
				if(course != null) {
					//load assignment information
					//TODO
				} else {
					assignTable.clearSelection();
					assignTable.setEnabled(false);
				}
					
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
