package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import soft.MAC_samples;
import java.awt.Font;
import java.awt.Color;

public class InsertAPDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField MacAddtf;
	private JTextField essidtf;
	private JTextField channeltf;

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			InsertAPDialog dialog = new InsertAPDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	int x;
	int y;
//	boolean isApPosSet = false;
	boolean macExists = false;
	int macIndex = PaintPane.Mac.size();
	
	private JTextField YTextF;
	private JTextField XTextF;
	final JCheckBox chckbxAuth;
	public InsertAPDialog(final PaintPane p) {
		setBounds(100, 100, 379, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblMacAddress = new JLabel("MAC Address");
			lblMacAddress.setBounds(10, 4, 105, 35);
			lblMacAddress.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblMacAddress);
		}
		
			final JLabel lblWarning = new JLabel("Warning: you are editing an existing AP's settings");
			lblWarning.setForeground(Color.RED);
			lblWarning.setFont(new Font("Tahoma", Font.PLAIN, 10));
			lblWarning.setBounds(31, 32, 322, 14);
			lblWarning.setVisible(false);
			contentPanel.add(lblWarning);
		
		{
			MacAddtf = new JTextField();
			MacAddtf.setBounds(125, 11, 157, 21);
			contentPanel.add(MacAddtf);
			MacAddtf.setColumns(10);
		}
		{
			JLabel lblEssid = new JLabel("ESSID");
			lblEssid.setBounds(10, 50, 105, 35);
			lblEssid.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblEssid);
		}
		{
			essidtf = new JTextField();
			essidtf.setBounds(125, 57, 157, 21);
			contentPanel.add(essidtf);
			essidtf.setColumns(10);
		}
		
			final JLabel lblx = new JLabel("X :");
			lblx.setBounds(125, 142, 18, 35);
			lblx.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblx);
		
		{
			JButton btnSetPosition = new JButton("Set Position");
			btnSetPosition.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
					MainWindow.panelCanvas.apPosition();
				}
			});
			{
				JLabel lblY = new JLabel("Y :");
				lblY.setHorizontalAlignment(SwingConstants.CENTER);
				lblY.setBounds(191, 142, 18, 35);
				contentPanel.add(lblY);
			}
			btnSetPosition.setBounds(258, 149, 26, 21);
			contentPanel.add(btnSetPosition);
		}
		
			chckbxAuth = new JCheckBox("Athorization");
			chckbxAuth.setBounds(10, 186, 118, 35);
			chckbxAuth.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(chckbxAuth);
		
		{
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addAP(chckbxAuth.isSelected());
					p.repaint();
				}
			});
			btnAdd.setBounds(171, 192, 89, 23);
			contentPanel.add(btnAdd);
		}
		
		JLabel lblChannel = new JLabel("Channel");
		lblChannel.setHorizontalAlignment(SwingConstants.CENTER);
		lblChannel.setBounds(10, 96, 105, 35);
		contentPanel.add(lblChannel);
		
		channeltf = new JTextField();
		channeltf.setColumns(10);
		channeltf.setBounds(125, 103, 157, 21);
		contentPanel.add(channeltf);
		{
			YTextF = new JTextField("0");
			YTextF.setBounds(211, 149, 37, 20);
			contentPanel.add(YTextF);
			YTextF.setColumns(10);
		}
		{
			XTextF = new JTextField("0");
			XTextF.setColumns(10);
			XTextF.setBounds(144, 149, 37, 20);
			contentPanel.add(XTextF);
		}
		{
			JLabel lblLocation = new JLabel("Location");
			lblLocation.setHorizontalAlignment(SwingConstants.CENTER);
			lblLocation.setBounds(10, 142, 105, 35);
			contentPanel.add(lblLocation);
		}
		{
			JButton btnExist = new JButton("check");
			btnExist.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					macExists = false ;
					lblWarning.setVisible(false);
					if (!MacAddtf.getText().isEmpty())
					{
						for (int i = 0 ; i < PaintPane.Mac.size(); i++)
						{
							if (PaintPane.Mac.get(i).getMacAddress().equals(MacAddtf.getText()) || PaintPane.Mac.get(i).getMac_Address().equals(MacAddtf.getText()))
							{
								macExists = true ;
								lblWarning.setVisible(true);
								essidtf.setText(PaintPane.Mac.get(i).getESSID());
								channeltf.setText(Integer.toString(PaintPane.Mac.get(i).getChannel()));
								XTextF.setText(Integer.toString(PaintPane.Mac.get(i).getApX()));
								YTextF.setText(Integer.toString(PaintPane.Mac.get(i).getApY()));
								chckbxAuth.setSelected(PaintPane.Mac.get(i).isAuthorized());
								return;
							}
						}
					}
					reset();
				}
			});
			btnExist.setBounds(287, 10, 66, 23);
			contentPanel.add(btnExist);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Done");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	public void addAP(boolean isAuth)
	{
		for (int i = 0 ; i < PaintPane.Mac.size(); i++)
		{
			if (PaintPane.Mac.get(i).getMacAddress().equals(MacAddtf.getText()) || PaintPane.Mac.get(i).getMac_Address().equals(MacAddtf.getText()))
			{
				PaintPane.Mac.get(i).setLocation(Integer.parseInt(XTextF.getText())	, Integer.parseInt(YTextF.getText()));
				PaintPane.Mac.get(i).setChannel(Integer.parseInt(channeltf.getText()));
				PaintPane.Mac.get(i).setAuthorized(isAuth);
				PaintPane.Mac.get(i).setESSID(essidtf.getText());
				return;
			}
		}
		int channel=0;
		try
		{
			channel = Integer.parseInt(channeltf.getText());
		}catch (Exception e)
		{ }
		PaintPane.Mac.add(new MAC_samples(essidtf.getText(), MacAddtf.getText(), channel , isAuth, Integer.parseInt(XTextF.getText()), Integer.parseInt(YTextF.getText())));
		
		reset();
	}
	
	public void setXY(int newx, int newy)
	{
		x = newx ;
		y = newy ;
		
		XTextF.setText(Integer.toString(x));
		YTextF.setText(Integer.toString(y));
	}
	
	public void reset()
	{
		essidtf.setText("");
		channeltf.setText("");
		XTextF.setText("0");
		YTextF.setText("0");
		chckbxAuth.setSelected(false);
	}
}
