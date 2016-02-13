package com.g10.portfolio1.server;

import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Controller implementation for the
 * server's MVC pattern.
 *
 */
public class ServerController {
	
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
		UserListModel l = (UserListModel) event.getSource();
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
class SelectedListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (isSelected) {
            c.setBackground(Color.YELLOW);
        }
        return c;
    }
}