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

	/**
	 * Create the frame.
	 */
	public ServerView() {
		
		setupGUI();
		new ServerLogin(listModel);
	}
	
	/**
	 * Initializes GUI contents and fills frame.
	 */
	private void setupGUI() {
		JFrame frame = new JFrame();
		frame.setTitle("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(800, 100, 205, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		frame.setContentPane(contentPane);
		
		JLabel lUsers = new JLabel("User List");
		lUsers.setFont(new Font("Tahoma", Font.BOLD, 16));
		lUsers.setBounds(49, 23, 77, 31);
		contentPane.add(lUsers);

		contentPane.add(getUserListPanel());
		frame.setVisible(true);		
	}
	
	private JPanel getUserListPanel() {
		//Initialize view components
		JPanel listPanel = new JPanel();
		listPanel.setBounds(10, 56, 171, 195);
		userList = new JList<>();

		//During the JList initialization...
		userList.setCellRenderer(new SelectedListCellRenderer());
				
		File userFile = new File(
			"C:\\Users\\ejshackelford\\java\\workspace\\coms319\\src\\com\\g10\\portfolio1\\resources\\server\\users.txt");
//			"C:\\Users\\Brody\\Desktop\\iastate\\Spring2016\\ComS319\\Lab2-Swing\\src\\com\\g10\\portfolio1\\resources\\server\\users.txt");
		
		//Model of the List
		listModel = new UserListModel(userFile);
		userList.setModel(listModel);
		
		listModel.addListDataListener(new UserListDataListener());
		
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
				try {
					listModel.removeElement(userList.getSelectedIndex()); 
				} catch (Exception e) {}
			}
		});
		buttonPanel.add(removeButton);
		
		return buttonPanel;
	}

}
