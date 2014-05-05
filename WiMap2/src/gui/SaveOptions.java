package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JLabel;

import java.awt.GridLayout;

import javax.swing.JInternalFrame;
import javax.swing.BoxLayout;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveOptions extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			SaveOptions dialog = new SaveOptions();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	private static JScrollPane jsp;

	public SaveOptions(final PaintPane p, final BufferedImage bi) {
		setBounds(100, 100, 350, 229);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{237, 0, 75, 83, 117, 0};
		gbl_contentPanel.rowHeights = new int[]{64, 23, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
				
						JLabel lblNewLabel = new JLabel("Check the Items on the list you would like to save");
						GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
						gbc_lblNewLabel.gridwidth = 2;
						gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
						gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
						gbc_lblNewLabel.gridx = 1;
						gbc_lblNewLabel.gridy = 0;
						contentPanel.add(lblNewLabel, gbc_lblNewLabel);
						
								final JCheckBox chckbxSaveData = new JCheckBox("Save Data");
								GridBagConstraints gbc_chckbxSaveData = new GridBagConstraints();
								gbc_chckbxSaveData.anchor = GridBagConstraints.WEST;
								gbc_chckbxSaveData.insets = new Insets(0, 0, 5, 5);
								gbc_chckbxSaveData.gridx = 1;
								gbc_chckbxSaveData.gridy = 1;
								contentPanel.add(chckbxSaveData, gbc_chckbxSaveData);
				
						final JCheckBox chckbxSaveImage = new JCheckBox("Save Image");
						chckbxSaveImage.setHorizontalAlignment(SwingConstants.LEFT);
						GridBagConstraints gbc_chckbxSaveImage = new GridBagConstraints();
						gbc_chckbxSaveImage.anchor = GridBagConstraints.WEST;
						gbc_chckbxSaveImage.insets = new Insets(0, 0, 5, 5);
						gbc_chckbxSaveImage.gridx = 1;
						gbc_chckbxSaveImage.gridy = 2;
						contentPanel.add(chckbxSaveImage, gbc_chckbxSaveImage);
						if (bi == null)
							chckbxSaveImage.setEnabled(false);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				if (!chckbxSaveData.isSelected() && !chckbxSaveImage.isSelected())
					dispose();
				
				if (chckbxSaveImage.isSelected())
				{
					if(bi == null)	// check if there is no image if the user asked to save an image
					{
						JOptionPane.showMessageDialog(null, "No image to save");
					}
					if (!chckbxSaveData.isSelected())
						dispose();
				}
				String userhome = System.getProperty("user.dir");
				JFileChooser saveFC = new JFileChooser(userhome);	//let the user browse for an image  
				
				saveFC.setAcceptAllFileFilterUsed(false);
//					saveFC.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif"));
				if (chckbxSaveImage.isSelected())
				{
					saveFC.addChoosableFileFilter(new FileNameExtensionFilter("png", "png"));
//					saveFC.addChoosableFileFilter(new FileNameExtensionFilter("JPEG", "JPEG"));
//					saveFC.addChoosableFileFilter(new FileNameExtensionFilter("gif", "gif"));
//					saveFC.addChoosableFileFilter(new FileNameExtensionFilter("tif" ,"tif"));
					
//					saveFC.addChoosableFileFilter(new ImageFilter());
				}else
				{
					saveFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
			
				
				int fileToSave = saveFC.showSaveDialog(jsp);
				String saveImagePath = null;

				if (fileToSave == JFileChooser.APPROVE_OPTION) 
				{
					File file = saveFC.getSelectedFile();
					boolean flag = true;
					try {
						saveImagePath = file.getAbsolutePath();
						if (chckbxSaveImage.isSelected())
						{
							String[] exts ;
							exts = ((FileNameExtensionFilter)saveFC.getFileFilter()).getExtensions();
							if (saveFC.getFileFilter() instanceof FileNameExtensionFilter) 
							{
								String nameLower = file.getName().toLowerCase();
								for (String ext : exts) { // check if it already has a valid extension	
									if (nameLower.endsWith('.' + ext.toLowerCase())) {
										saveImagePath = file.getAbsolutePath(); // 
										flag = false ;
										break;
									}
								}
								// if not, append the first extension from the selected filter
								if (flag)
									saveImagePath = saveImagePath + '.' + exts[0];
							}
							System.out.println("SAVING IMAGE");
							BufferedImage saving = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
							Graphics graphics = saving.createGraphics();
							p.paint(graphics);
							Graphics g = p.getGraphics();
							g.dispose();
							File map = new File(saveImagePath);		//there must be a way
							ImageIO.write(saving, exts[0], map);
							if (chckbxSaveData.isSelected())
							{
								File f = new File (file.getParent() + "\\Data\\");
								f.getParentFile().mkdir();
								System.out.println(file.getParent() + "\\Data\\");
								PaintPane.saveMacSamples(file.getParent() + "\\Data\\");	//save the data								
							}
						}
						else if (chckbxSaveData.isSelected())
						{
							System.out.println(saveFC.getSelectedFile().getAbsolutePath());
							PaintPane.saveMacSamples(saveFC.getSelectedFile().getAbsolutePath());	//save the data
						}

					} catch(IOException exc) {
						exc.printStackTrace();
					}catch (Exception e1)
					{
						e1.printStackTrace();

					}
				}
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);	
	}
}
