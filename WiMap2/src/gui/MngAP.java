package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import soft.MAC_samples;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MngAP extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	final JList<String> authAPList;
	final JList<String> avAPList;
	final DefaultListModel<String> lm0;
	final DefaultListModel<String> lm;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MngAP() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 470, 324);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 47, 129, 156);
		contentPane.add(scrollPane);


		//add MAC addresses of authorized AP's to avAPList
		lm0 = new DefaultListModel();
		for(int x = 0; x < PaintPane.authAPs.size(); x++)
		{
			lm0.addElement(PaintPane.authAPs.get(x).getMacAddress());
		}
		authAPList = new JList(lm0);

		scrollPane.setViewportView(authAPList);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(302, 47, 129, 156);
		contentPane.add(scrollPane_1);

		//add MAC addresses of authorized AP's to avAPList
		lm = new DefaultListModel();
		for(int x = 0; x < PaintPane.Mac.size(); x++)
		{
			lm.addElement(PaintPane.Mac.get(x).getMacAddress());
		}
		avAPList = new JList(lm);
		scrollPane_1.setViewportView(avAPList);

		JButton addBtn = new JButton("<<");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String mac = (String) avAPList.getSelectedValue();
				addToAuthAPList(mac);

			}
		});
		addBtn.setBounds(172, 73, 103, 23);
		contentPane.add(addBtn);

		JButton rmvBtn = new JButton(">>");
		rmvBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { //remove the selected item from authAPList
				String mac = (String) authAPList.getSelectedValue();
				if (lm0.contains(mac))
				{
					lm0.removeElement(mac);

					if (getEntryFromAuthArray(mac) != null)
					{	
						PaintPane.authAPs.remove(getEntryFromAuthArray(mac));
					}
				}

			}
		});

		rmvBtn.setBounds(172, 110, 103, 23);
		contentPane.add(rmvBtn);

		JButton addAPBtn = new JButton("Add other ...");
		addAPBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String res = JOptionPane.showInputDialog("MAC address of your AP:");
				addToAuthAPList(res);
				addToAvAPList(res);
			}
		});
		addAPBtn.setBounds(172, 144, 103, 23);
		contentPane.add(addAPBtn);

		JLabel lblAuthorizedAps = new JLabel("Authorized AP's");
		lblAuthorizedAps.setBounds(20, 30, 129, 14);
		contentPane.add(lblAuthorizedAps);

		JLabel lblAvailableAps = new JLabel("Available AP's");
		lblAvailableAps.setBounds(304, 30, 127, 14);
		contentPane.add(lblAvailableAps);

		super.setDefaultCloseOperation(HIDE_ON_CLOSE);
		super.setResizable(false);
		super.setVisible(true);
	}

	private void addToAuthAPList(String mac) {
		if (!lm0.contains(mac))
		{
			lm0.addElement(mac);

			if (PaintPane.getEntryFromMACArray(mac) != null)
			{	
				PaintPane.authAPs.add(PaintPane.getEntryFromMACArray(mac));
			}
		}
	}
	
	private void addToAvAPList(String mac) {
		if (!lm.contains(mac))
		{
			lm.addElement(mac);

			if (PaintPane.getEntryFromMACArray(mac) != null)
			{	
				PaintPane.Mac.add(PaintPane.getEntryFromMACArray(mac));
			}
		}
	}

	public MAC_samples getEntryFromAuthArray(String mac)
	{
		/*
		 * Given a MAC address in string format, return the corresponding entry in the authAPs list
		 */

		for (int i = 0; i < PaintPane.authAPs.size(); i++)
		{
			if (mac.equals(PaintPane.authAPs.get(i).getMacAddress()))
			{
				return PaintPane.authAPs.get(i);
			}
			else
				continue;
		}
		return null;
	}
}
