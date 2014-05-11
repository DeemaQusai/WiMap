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

import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveOptions extends JDialog {

	private static final long serialVersionUID = 1L;

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
		setBounds(100, 100, 416, 170);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
						contentPanel.setLayout(null);
				
						JLabel lblNewLabel = new JLabel("Check the Items on the list you would like to save");
						lblNewLabel.setBounds(24, 12, 386, 23);
						contentPanel.add(lblNewLabel);
						
								final JCheckBox chckbxSaveData = new JCheckBox("Save Data");
								chckbxSaveData.setBounds(67, 70, 97, 23);
								contentPanel.add(chckbxSaveData);
				
						final JCheckBox chckbxSaveImage = new JCheckBox("Save Image");
						chckbxSaveImage.setBounds(67, 43, 107, 23);
						chckbxSaveImage.setHorizontalAlignment(SwingConstants.LEFT);
						chckbxSaveImage.setVisible(true);
						contentPanel.add(chckbxSaveImage);
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
								File f = new File (file.getParent() + "/Data/");
								f.getParentFile().mkdir();
								System.out.println(file.getParent() + "/Data/");
								PaintPane.saveMacSamples(file.getParent() + "/Data/");	//save the data								
							}
						}
						else if (chckbxSaveData.isSelected())
						{
							System.out.println(saveFC.getSelectedFile().getAbsolutePath());
							PaintPane.saveMacSamples(saveFC.getSelectedFile().getAbsolutePath()+"/");	//save the data
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
