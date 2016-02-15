package com.g10.portfolio1.server;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * View implementation for the
 * server's MVC pattern.
 *
 */
public class ServerView {

	private JPanel contentPane;
	private UserListModel listModel;
	private JList<String> userList;
	private PassListModel passModel;
	private JList<String> passList;

	/**
	 * Create the frame.
	 */
	public ServerView() {
		
		setupGUI();
		
		// Starts server login listener
		Thread loginListener = new Thread(new ServerLogin(listModel, passModel));
		loginListener.start();
	}
	
	/**
	 * Initializes GUI contents and fills frame.
	 */
	private void setupGUI() {
		JFrame frame = new JFrame();
		frame.setTitle("Grade Tracker | Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(800, 100, 300, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		frame.setContentPane(contentPane);
		
		JLabel lUsers = new JLabel("User List");
		lUsers.setFont(new Font("Tahoma", Font.BOLD, 16));
		lUsers.setBounds(100, 23, 77, 31);
		contentPane.add(lUsers);

		contentPane.add(getUserListPanel());
		frame.setVisible(true);		
	}
	
	private JPanel getUserListPanel() {
		//Initialize view components
		JPanel listPanel = new JPanel();
		listPanel.setBounds(10, 56, 265, 195);
		userList = new JList<>();
		passList = new JList<>();

		//During the JList initialization...
		userList.setCellRenderer(new SelectedListCellRenderer());
				
		File userFile = new File("src\\com\\g10\\portfolio1\\resources\\server\\users.txt");
		File passFile = new File("src\\com\\g10\\portfolio1\\resources\\server\\passwords.txt");
		
		//Model of the Lists
		listModel = new UserListModel(userFile);
		passModel = new PassListModel(passFile);
		userList.setModel(listModel);
		passList.setModel(passModel);
		
		listModel.addListDataListener(new UserListDataListener());
		passModel.addListDataListener(new PassListDataListener());
		
		//Add list to scroll pane
		JScrollPane scrollPane = new JScrollPane(userList);
		listPanel.add(scrollPane);
		
		listPanel.add(getUserListButtonsPanel());
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		
		return listPanel;
	}

	private JPanel getUserListButtonsPanel() {
		JPanel buttonPanel = new JPanel();
		JButton removeButton = new JButton("Remove");
		
		//action listener for remove button
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = userList.getSelectedIndex();
				listModel.removeElement(index);
				passModel.removeElement(index);
			}
		});
		buttonPanel.add(removeButton);
		
		return buttonPanel;
	}

}
