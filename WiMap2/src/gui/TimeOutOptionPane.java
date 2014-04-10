package gui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;


/*
 * This is the message box that appears after the users clicks on a map,
 * it counts down then automatically exits
 */

public class TimeOutOptionPane extends JOptionPane implements Runnable {

	private static final long serialVersionUID = 1L;
	
	Component parent = null;
	Object msg = null;
	String title = null;
	int optType = 0;
	int msgType = 0;
	Object[] opts = null;
	Object initVal = null;

	public TimeOutOptionPane(Component parentComponent, Object message, final String title, int optionType, int messageType, Object[] options, final Object initialValue) {
		super();
		this.parent = parentComponent;
		this.msg = message;
		this.title = title;
		this.optType = optionType;
		this.msgType = messageType;
		this.opts = options;
		this.initVal = initialValue;
	}

	final static int PRESET_TIME = 2;	//the time the shell takes to give the result

	public int showTimeoutDialog(Component parentComponent, Object message, final String title, int optionType,
			int messageType, Object[] options, final Object initialValue) {
		JOptionPane pane = new JOptionPane(message, messageType, optionType, null, options, initialValue);

		pane.setInitialValue(initialValue);

		final JDialog dialog = pane.createDialog(parentComponent, title);

		pane.selectInitialValue();
		new Thread() {
			public void run() {
				try {
					for (int i=PRESET_TIME; i>=0; i--) {
						Thread.sleep(1000);
						if (dialog.isVisible() && i<300) {
							dialog.setTitle(title + "  (" + i + " seconds before closing)");
						}
					}
					if (dialog.isVisible()) {
						dialog.setVisible(false);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}.start();
		dialog.setVisible(true);

		Object selectedValue = pane.getValue();
		if (selectedValue.equals("uninitializedValue")) {
			selectedValue = initialValue;
		}
		if (selectedValue == null)
			return CLOSED_OPTION;
		if (options == null) {
			if (selectedValue instanceof Integer)
				return ((Integer) selectedValue).intValue();
			return CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue))
				return counter;
		}
		return CLOSED_OPTION;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		showTimeoutDialog(parent, msg, title, optType, msgType, opts, initVal);
	}
}