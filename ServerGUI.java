package com.g10.portfolio1;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

public class ServerGUI extends JFrame {

	private JPanel contentPane;
	private DataModel listModel;
	private JList<String> myList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerGUI() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lUsers = new JLabel("User List");
		lUsers.setFont(new Font("Tahoma", Font.BOLD, 16));
		lUsers.setBounds(49, 23, 77, 31);
		contentPane.add(lUsers);

		contentPane.add(getMyListPanel());
	}
	
	private JPanel getMyListPanel() {
		//Initialize view components
		JPanel listPanel = new JPanel();
		listPanel.setBounds(10, 56, 171, 195);
		myList = new JList<>();

		//During the JList initialization...
		myList.setCellRenderer(new SelectedListCellRenderer());
				
		File usersFile = new File(
			"C:\\Users\\ejshackelford\\java\\workspace\\coms319\\src\\com\\g10\\portfolio1\\users.txt");
//			"C:\\Users\\Brody\\Desktop\\iastate\\Spring2016\\ComS319\\Lab2-Swing\\src\\com\\g10\\portfolio1\\users.txt");
		
		//Model of the List
		listModel = new DataModel(usersFile);
		myList.setModel(listModel);
		
		listModel.addListDataListener(new MyListDataListener());
		
		//Add list to scroll pane
		JScrollPane scrollPane = new JScrollPane(myList);
		listPanel.add(scrollPane);
		
		listPanel.add(getMyListButtonsPanel());
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		
		return listPanel;
	}

	private JPanel getMyListButtonsPanel() {
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
					listModel.removeElement(myList.getSelectedIndex()); 
				} catch (Exception e) {}
			}
		});
		
//		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		return buttonPanel;
	}
}

/**
 * Data model for the user list.
 *
 */
@SuppressWarnings("serial")
class DataModel extends AbstractListModel<String> {
	private ArrayList<String> myList = null;
	private File myFile = null;
	
	public DataModel(File file) {
		Scanner scan = null;
		myList = new ArrayList<>();
		myFile = file;
		
		// scans the file and adds users to list
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine())
				myList.add(scan.nextLine());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}
	
	@Override
	public String getElementAt(int arg0) {
		return myList.get(arg0);
	}

	@Override
	public int getSize() {
		return myList.size();
	}
	
	public void addElement (String arg) {
		myList.add(arg);
		this.fireIntervalAdded(this,myList.size()-1, myList.size()-1);
	}
	
	public void removeElement (int index) {
		myList.remove(index);
		this.fireIntervalRemoved(this, myList.size()-1, myList.size()-1);
	}
	
	public File getFile() {
		return myFile;
	}
}

/**
 * Observes for changes in user list and 
 * initiates correct action accordingly.
 * 
 */
class MyListDataListener implements ListDataListener {

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
