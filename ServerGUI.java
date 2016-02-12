package com.g10.portfolio1;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class ServerGUI {

	private JPanel contentPane;
	private DataModel listModel;
	private JList<String> userList;
//	private Map<Socket, String> connections;
	ServerSocket serverSocket;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ServerGUI frame = new ServerGUI();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ServerGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setupGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		openLoginSocket();
	}
	
	private void setupGUI() {
		JFrame frame = new JFrame();
		frame.setTitle("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		
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
				
		File usersFile = new File(
			"C:\\Users\\ejshackelford\\java\\workspace\\coms319\\src\\com\\g10\\portfolio1\\users.txt");
//			"C:\\Users\\Brody\\Desktop\\iastate\\Spring2016\\ComS319\\Lab2-Swing\\src\\com\\g10\\portfolio1\\users.txt");
		
		//Model of the List
		listModel = new DataModel(usersFile);
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
//		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		
		//action listener for add button
//		addButton.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String response = JOptionPane.showInputDialog(null, "What is the new company?", "Enter new company name", JOptionPane.QUESTION_MESSAGE);
//				listModel.addElement(response);
//			}
//		});
		
		//action listener for remove button
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					listModel.removeElement(userList.getSelectedIndex()); 
				} catch (Exception e) {}
			}
		});
		
//		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		return buttonPanel;
	}

	/**
	 * Open socket to service login requests.
	 */
	private void openLoginSocket() {
		
//		connections = new HashMap<>();
		serverSocket = null;
		
		try {
			// Create server socket
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port: 4444");
			System.exit(-1);
		}
		listen();
	}

	/**
	 * Listen for clients to connect and 
	 * fork new thread for each client.
	 */
//	@SuppressWarnings("resource")
	private void listen() {
		
		// Wait for connections
		while (true) {
			Socket clientSocket = null;
			Scanner in;
			String name;
			
			try {
				clientSocket = serverSocket.accept();
				in = new Scanner(clientSocket.getInputStream());
				name = in.nextLine();
				Thread t = new Thread(new LoginHandler(clientSocket));
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}
}

class LoginHandler implements Runnable {
	// Handles connection to client
	Socket s;
	// Username of connected client
	String name;
	
	LoginHandler(Socket s) {
		this.s = s;
		Scanner in;
		try {
			in = new Scanner(s.getInputStream());
			name = in.nextLine();
			in.close();
			System.out.println(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		// Scanner to read input from client
		Scanner in;
	//	ArrayList<String> sendList = new ArrayList<>();
		String clientMessage;
	//	ArrayList<Socket> socketList;
		
		try {
			in = new Scanner(s.getInputStream());
			clientMessage = in.nextLine();
	//		while(in.hasNextLine())
	//			sendList.add(in.nextLine());
			// Send messages to selected users
			System.out.println(name);
			System.out.println(clientMessage);
	//		for(String s : sendList) {
	//			
	//		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
//  ****** GOOD WAY TO PURPOSELY KILL A QUICK USE THREAD ********
//	
//	class MyThread extends Thread
//	{
//	  volatile boolean finished = false;
//
//	  public void stopMe()
//	  {
//	    finished = true;
//	  }
//
//	  public void run()
//	  {
//	    while (!finished)
//	    {
//	      //do dirty work
//	    }
//	  }
//	}
	
	//void printSocketInfo(Socket s) {
	//	System.out.print("Socket on Server " + Thread.currentThread() + " ");
	//	System.out.print("Server socket Local Address: " + s.getLocalAddress()
	//			+ ":" + s.getLocalPort());
	//	System.out.println("  Server socket Remote Address: "
	//			+ s.getRemoteSocketAddress());
	//}
}

/**
 * Data model for the user list.
 *
 */
@SuppressWarnings("serial")
class DataModel extends AbstractListModel<String> {
	private ArrayList<String> userList = null;
	private File userFile = null;
	
	public DataModel(File file) {
		Scanner scan = null;
		userList = new ArrayList<>();
		userFile = file;
		
		// scans the file and adds users to list
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine())
				userList.add(scan.nextLine());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}
	
	@Override
	public String getElementAt(int arg0) {
		return userList.get(arg0);
	}

	@Override
	public int getSize() {
		return userList.size();
	}
	
	public void addElement (String arg) {
		userList.add(arg);
		this.fireIntervalAdded(this,userList.size()-1, userList.size()-1);
	}
	
	public void removeElement (int index) {
		userList.remove(index);
		this.fireIntervalRemoved(this, userList.size()-1, userList.size()-1);
	}
	
	public File getFile() {
		return userFile;
	}
}

/**
 * Observes for changes in user list and 
 * initiates correct action accordingly.
 * 
 */
class UserListDataListener implements ListDataListener {

	/**
	 * Adds user to users file when added to list.
	 */
	@Override
	public void contentsChanged(ListDataEvent event) {
		PrintWriter pw = null;
		DataModel l = (DataModel) event.getSource();
		l.getElementAt(event.getIndex0());
		try {
			pw = new PrintWriter(l.getFile());
			int size = l.getSize();
			for (int i = 0; i<size; i++) {
				pw.println(l.getElementAt(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

	/**
	 * Detects if item was added to list.
	 */
	@Override
	public void intervalAdded(ListDataEvent event) {
		contentsChanged(event);
	}

	/**
	 * Detects if item was removed from list.
	 */
	@Override
	public void intervalRemoved(ListDataEvent event) {
		contentsChanged(event);
	}
	
}

/**
 * Sets background of selected user to yellow.
 * 
 */
@SuppressWarnings("serial")
class SelectedListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (isSelected) {
            c.setBackground(Color.YELLOW);
        }
        return c;
    }
}
