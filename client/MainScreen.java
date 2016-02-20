package com.g10.portfolio1.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

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
import javax.swing.ListSelectionModel;
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
	private JButton editRow;
	private final String[] tableHeaders = { "Title", "Category", "Date", "Points Earned", "Points Possible" };
	private String username;
	private LoginStatus loginStatus;
	// for requesting info from server
	private Socket receiveSocket;
	// to save progress to server
	private Socket sendSocket;
	
	// Key - semester, Value - list of courses
	private HashMap<String, ArrayList<String>> hmSemCourses;
	// Key - course, Value - assignments (DefaultTableModel)
	private HashMap<String, DefaultTableModel> hmCourseAssign;

	public MainScreen(LoginStatus ls) {
		// get user information
		loginStatus = ls;
		username = loginStatus.getName();
		// create connections to server for requesting and saving files
		connect();

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

	// returns panel with assignment view components
	private JPanel getAssignmentPanel() {

		JPanel assignPanel = new JPanel();
		assignPanel.setLayout(new BorderLayout());
		assignPanel.add(new JLabel("<html><p>Assignments:</p></html>"), BorderLayout.PAGE_START);

		tableModel = createTableModel();
		assignTable = new JTable(tableModel);
		assignTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Make the JTable scrollable
		JScrollPane tableScroll = new JScrollPane(assignTable);

		assignPanel.add(tableScroll);

		assignPanel.add(getAssignmentButtonsPanel(), BorderLayout.PAGE_END);
		assignTable.setEnabled(false);

		//getAssignments();

		return assignPanel;
	}

	// Return a panel with the add, edit, and remove buttons
	private JPanel getAssignmentButtonsPanel() {

		JPanel assignBP = new JPanel();
		assignBP.setLayout(new FlowLayout());

		JButton addRow = new JButton("Add row");
		editRow = new JButton("Edit table");
		JButton remRow = new JButton("Remove row");

		// Add listeners for each button
		addRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((courseCBModel.getSelectedItem() != null) && !(courseCBModel.getSelectedItem().equals(""))) {
					String[] row = { "", "", "", "", "" };
					hmCourseAssign.get(courseCBModel.getSelectedItem()).addRow(row);
					tableModel = hmCourseAssign.get(courseCBModel.getSelectedItem());
					assignTable.setModel(tableModel);
				}
			}
		});

		remRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((courseCBModel.getSelectedItem() != null) && !(courseCBModel.getSelectedItem().equals(""))) {
					tableModel = hmCourseAssign.get(courseCBModel.getSelectedItem());
					assignTable.setModel(tableModel);
					if (tableModel.getRowCount() != 0) {
						tableModel.removeRow(tableModel.getRowCount() - 1);
					}
				}
			}
		});

		editRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((courseCBModel.getSelectedItem() != null) && !(courseCBModel.getSelectedItem().equals(""))) {
					String label = editRow.getText();
					if (label.equals("Edit table")) {
						tableModel = hmCourseAssign.get(courseCBModel.getSelectedItem());
						assignTable.setModel(tableModel);
						assignTable.setEnabled(true);
						assignTable.changeSelection(0, 1, false, false);
						editRow.setText("Lock table");

						// Disable CB's
						courseCB.setEnabled(false);
						semesterCB.setEnabled(false);
					}
					// Lock the table, send info to server
					else {
						tableModel = hmCourseAssign.get(courseCBModel.getSelectedItem());
						assignTable.setModel(tableModel);
						saveToServer();
						assignTable.clearSelection();
						assignTable.setEnabled(false);
						editRow.setText("Edit table");

						// Enable CB's
						courseCB.setEnabled(true);
						semesterCB.setEnabled(true);
					}
				}
			}
		});

		assignBP.add(addRow);
		assignBP.add(editRow);
		assignBP.add(remRow);

		return assignBP;
	}

	private DefaultTableModel createTableModel() {
		DefaultTableModel tm;
		tm = new DefaultTableModel(tableHeaders, 10);

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
		// Set add semester button functionality
		addSemesterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editRow.getText().equals("Edit table")) {
					String response = JOptionPane.showInputDialog(null, "What is the new semester?",
							"Enter new semester", JOptionPane.QUESTION_MESSAGE);
					if ((response != null) && !(response.equals(""))) {
						hmSemCourses.put(response, new ArrayList<>());
						semCBModel.addElement(response);
						courseCB.setEnabled(true);
						semCBModel.setSelectedItem(response);
					}
				}
			}
		});

		// Set remove semester button functionality
		remSemesterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editRow.getText().equals("Edit table")) {
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
			}
		});

		addCourseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editRow.getText().equals("Edit table")) {
					if (courseCB.isEnabled()) {
						String response = JOptionPane.showInputDialog(null, "What is the new course?",
								"Enter new course", JOptionPane.QUESTION_MESSAGE);
						if ((response != null) && !(response.equals(""))) {
							hmSemCourses.get(semCBModel.getSelectedItem()).add(response);
							courseCBModel.addElement(response);
							courseCB.setModel(courseCBModel);
							courseCBModel.setSelectedItem(response);

							hmCourseAssign.put(response, createTableModel());
							assignTable.setModel(hmCourseAssign.get(response));
						}
					}
				}
			}
		});

		// Set remove semester button functionality
		remCourseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editRow.getText().equals("Edit table")) {
					if (courseCBModel.getSize() != 0) {
						hmSemCourses.get(semesterCB.getSelectedItem()).remove(semesterCB.getSelectedIndex());
						courseCBModel.removeElementAt(courseCB.getSelectedIndex());

						hmCourseAssign.remove(courseCB.getSelectedItem());
					} else {
						hmCourseAssign = new HashMap<>();

						assignTable.clearSelection();
						assignTable.setEnabled(false);
					}
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

	private void getSemAndCourses() {
		
		hmSemCourses = new HashMap<>();
		
		PrintWriter out;
		Scanner in = null;
		
		//Get semester and course information from server
		try {
			// get output and input stream
			out = new PrintWriter(receiveSocket.getOutputStream());
			in = new Scanner(receiveSocket.getInputStream());
			
			// send type of request to server
			out.println("<LIST>");
			out.println(username);
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String readStream = in.nextLine();
		String semester;
		ArrayList<String> courses = new ArrayList<>();
		
		// leave empty hash-map if no semesters
		if(readStream.equals("<COMPLETE>")) {
			return;
		}
		
		// keep reading contents until complete
		while(!readStream.equals("<COMPLETE>")) {
			
			semester = readStream;
			readStream = in.nextLine();
			
			// get all courses for semester
			while(!readStream.equals("<ENDSEMESTER>") && !readStream.equalsIgnoreCase("<COMPLETE>")) {
				courses.add(readStream);
				//.substring(0, readStream.length()-4));
				readStream = in.nextLine();
			}
			// add semester and courses
			if(!readStream.equals("<COMPLETE>")) {
				hmSemCourses.put(semester, courses);
				// check next semester or completion tag
				readStream = in.nextLine();
			}
		}
	}

	private void saveToServer() {

		// Save all semesters, courses, and assignments	
		DefaultTableModel tm = hmCourseAssign.get(courseCBModel.getSelectedItem());
		int numRows = tm.getRowCount();
		int numCol = tableHeaders.length;
		ArrayList<String> rowList = new ArrayList<>();
		StringBuilder row;
		
		for(int i = 0; i < numRows; ++i) {
			row = new StringBuilder();
			for(int j = 0; j < numCol; ++j) {
				
				if(j == (numCol-1))
					row.append(tm.getValueAt(i, j).toString());
				else
					row.append(tm.getValueAt(i, j).toString() + ',');
					
			}
			rowList.add(row.toString());
		}
		
		try {
			PrintWriter out = new PrintWriter(sendSocket.getOutputStream());
			out.println(username);
			out.println(semCBModel.getSelectedItem());
			out.println(courseCBModel.getSelectedItem());
			out.flush();
			for(String s : rowList) {
				out.println(s);
			}
			out.println("<COMPLETE>");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private JPanel getCourseSelectorPanel() {

		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));

		// Call server for info
		semCBModel = new DefaultComboBoxModel<>();
		courseCBModel = new DefaultComboBoxModel<>();

		JLabel semLabel = new JLabel("Semester:    ");
		semesterCB = new JComboBox<>(semCBModel);
		JLabel courseLabel = new JLabel("     Course:     ");
		courseCB = new JComboBox<>(courseCBModel);
		courseCB.setEnabled(false);
		
		getSemAndCourses();

		// Call Server for combo box lists
		ArrayList<String> semesterList = new ArrayList<>();
		semesterList = requestSemesterList();
		
		if(semesterList.size() != 0)
			courseCB.setEnabled(true);

		for (String element : semesterList)
			semCBModel.addElement(element);

		semesterCB.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				String semester = (String) semesterCB.getSelectedItem();
				if (semester != null) {
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

				if (course != null) {
					// get assignments for semester
					getAssignments();
					// load assignment information
					DefaultTableModel tm = hmCourseAssign.get(course);
					if (tm == null) {
						tm = new DefaultTableModel();
					}
					tableModel = tm;
					assignTable.setModel(tableModel);
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
	
	private ArrayList<String> requestSemesterList() {
		
		return new ArrayList<String>(hmSemCourses.keySet());
	}

	/**
	 * Get assignment info from server
	 */
	private void getAssignments() {
		
		hmCourseAssign = new HashMap<>();
		Scanner in = null;
		
		try {
			PrintWriter out = new PrintWriter(receiveSocket.getOutputStream());
			in = new Scanner(receiveSocket.getInputStream());
			
			// send type of request to server
			out.println("<ASSIGNMENT>");
			out.println(username);
			// get semester and course
			String semester = semCBModel.getSelectedItem().toString();
			String course = courseCBModel.getSelectedItem().toString();
			out.println(semester);
			out.println(course);
			out.flush();
			
			String fileLine = in.nextLine();
			
			Scanner scanLine = null;
			DefaultTableModel tm = createTableModel();
			
			boolean isAssignments = false;
			while(!fileLine.equals("<COMPLETE>")) {
				if(!isAssignments) {
					tm = new DefaultTableModel(tableHeaders, 0);
					isAssignments = true;
				}
				if(fileLine.equals("<ERROR>")) {
					JOptionPane.showMessageDialog(null, "Error receiving course information. Please try again.", 
							"Retrieve Course Error",  JOptionPane.ERROR_MESSAGE);
				} else {
					Vector<String> row = new Vector<>();
					// parse line, add to vector, add to table model
					scanLine = new Scanner(fileLine);
					scanLine.useDelimiter(",");
					String cell;
					while(scanLine.hasNext()) {
						cell = scanLine.next();
						row.add(cell);
					}
					tm.addRow(row);
					fileLine = in.nextLine();
				}
			}
			if(isAssignments) {
				hmCourseAssign.put(course, tm);
				scanLine.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() {
		
		// connect to port for receiving info from server
		try {
			receiveSocket = new Socket("localhost", 4445);
			System.out.println("Receive connection established...");
		} catch (IOException e) {
			System.out.println("Receive connection error on 4445...");
			e.printStackTrace();
		}
		// connect to port for saving info to server
		try {
			sendSocket = new Socket("localhost", 4446);
			loginStatus.setSendSocket(sendSocket);
			System.out.println("Send connection established...");
		} catch (IOException e) {
			System.out.println("Send connection error on 4446...");
			e.printStackTrace();
		}
	}

}
