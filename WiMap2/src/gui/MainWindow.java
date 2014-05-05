package gui;

import soft.getRSSI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.AbstractUndoableEdit;

public class MainWindow extends JPanel {
	private static final long serialVersionUID = 1L;
	private static BufferedImage image;
	public static PaintPane panelCanvas;
	private static JScrollPane jsp;
	private static JPanel sidePanel;
	private static JPanel authAP_sp;
	private static JTabbedPane tb;
	public static ArrayList<JPanel> av_net_panel = new ArrayList<JPanel> ();
	public static ArrayList<JCheckBox> chbxlist = new ArrayList<JCheckBox>() ;
	
	public static InsertAPDialog insertapdialog;
	static JFrame f ;
	private static JScrollPane sp ;
	static int windowColor = 0xE6E6FA;

	public static JMenuBar createMenuBar()
	{
		JMenuBar menuBarUpper = new JMenuBar();		//define and initialized menuBarUpper
		menuBarUpper.setBackground(new Color(windowColor));
		f.setJMenuBar(menuBarUpper);			//add the menuBarUpper to the frame

		JMenu fileMenu = new JMenu("File");		//create the "file" menu
		JMenu insertMenu = new JMenu("Insert");
		JMenu toolsMenu = new JMenu("Tools");
		JMenu HelpMenu = new JMenu("Help");		//create "help" menu

		menuBarUpper.add(fileMenu);							//add the "file" menu to the bar
		menuBarUpper.add(insertMenu);
		menuBarUpper.add(toolsMenu);
		menuBarUpper.add(HelpMenu);							//add "help" menu to the bar

		JMenuItem newItem = new JMenuItem("New");
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
/*		
		JMenuItem loadItem = new JMenuItem("Load");					//create the open
		loadItem.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
*/		
		final JMenuItem loadDataItem = new JMenuItem("Load Data");					//del

		final JMenuItem saveItem = new JMenuItem("Save");					//create the save
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		
		JMenuItem exit = new JMenuItem("Exit");						//create the exit
		
		final JMenuItem computePLEItem = new JMenuItem("Compute PLE");
		final JMenuItem insertAPItem = new JMenuItem("Insert AP");
		final JMenuItem SigmaItem = new JMenuItem("Insert Sigma");
		final JMenuItem manageAPsItem = new JMenuItem("Manage Access Points");
		final JMenuItem changeScaleItem = new JMenuItem("Change map scale");
		final JMenuItem smoothResItem = new JMenuItem("Smooth resolution");
		final JMenuItem HAPsItem = new JMenuItem("hypothetical AP’s");

		JMenuItem aboutItem = new JMenuItem("About WiMAP");			//create the about 

		newItem.setToolTipText("New project");		//tool tip (on mouse hover)
//		loadItem.setToolTipText("Open Floor Plan");		//tool tip (on mouse hover)
		saveItem.setToolTipText("Save Map");			//tool tip (on mouse hover)
		exit.setToolTipText("Exit application");		//tool tip (on mouse hover)
		smoothResItem.setToolTipText("Effects speed");		//??

		fileMenu.add(newItem);			//add the new to the "file" menu
//		fileMenu.add(loadItem);			//add the open to the "file" menu
		fileMenu.add(loadDataItem);		
		fileMenu.add(saveItem);			//add the save to the "file" menu
		fileMenu.add(exit);				//add the exit to the "file" menu
		
		insertMenu.add(insertAPItem);
		insertMenu.add(SigmaItem);
		insertMenu.add(HAPsItem);

		toolsMenu.add(computePLEItem);
		toolsMenu.add(manageAPsItem);
		toolsMenu.add(changeScaleItem);
		toolsMenu.add(smoothResItem);

		HelpMenu.add(aboutItem);		//add the about to the "Help" menu
		
		/*******************************File Menu******************************************/
		// NEW 
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				try {
					newActionPerformed (event);
				} catch (Exception e)
				{
					return;
				}
				saveItem.setEnabled(true);
				loadDataItem.setEnabled(true);
				insertAPItem.setEnabled(true);
				computePLEItem.setEnabled(true);
				manageAPsItem.setEnabled(true);
				changeScaleItem.setEnabled(true);
				smoothResItem.setEnabled(true);
				HAPsItem.setEnabled(true);
				SigmaItem.setEnabled(true);

			}
		});

		// OPEN
/*		loadItem.addActionListener(new ActionListener() {		//action listener for open
			public void actionPerformed(ActionEvent event) {
				boolean flag = true ;
				try {

					openActionPerformed (event);
				} catch (Exception e)
				{
					flag = false ;
				}
				saveItem.setEnabled(flag);
			}
		});
*/
		loadDataItem.addActionListener(new ActionListener() {		//action listener for open
			public void actionPerformed(ActionEvent event) {
				try {

					loadtextActionPerformed (event);
				} catch (Exception e)
				{
					return;
				}
				saveItem.setEnabled(true);
			}
		});		// SAVE
		saveItem.addActionListener(new ActionListener() {		//action listener for open
			public void actionPerformed(ActionEvent event) {
				saveActionPerformed (event);
			}		
		});
		saveItem.setEnabled(false);

		// EXIT
		exit.addActionListener(new ActionListener() {			//action listener for exit
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}

		});

		/*******************************Tool Menu******************************************/
		computePLEItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.compute_n();
			}
		});
		computePLEItem.setEnabled(false);
		
		manageAPsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new MngAP();
			}
		});
		manageAPsItem.setEnabled(false);
		
		insertAPItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					insertapdialog = new InsertAPDialog(panelCanvas);
					insertapdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					insertapdialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		insertAPItem.setEnabled(false);
		
		SigmaItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.InsertSigma();
			}
		});
		
		changeScaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.removeMouseListener(panelCanvas.samplingML);
				JOptionPane.showMessageDialog(null, "To rescale\nClick on two points and enter the real distance between them in meters" );
				panelCanvas.addMouseListener(panelCanvas.scalingML);				
			}
		});
		changeScaleItem.setEnabled(false);
		
		smoothResItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					resolutionDialog dialog = new resolutionDialog(panelCanvas);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
/*try {
					String input = JOptionPane.showInputDialog(null, "Enter size of square in pixels", ""); // ??
					if (panelCanvas.setSmoothRes(Integer.parseInt(input)))
						JOptionPane.showMessageDialog(null, "New smooth Resolution : " + panelCanvas.getSmoothRes());
					else
						JOptionPane.showMessageDialog(null, "Invalid input, will restore default resolution\nSmooth Resolution: " + panelCanvas.getSmoothRes());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}*/

			}
		});
		smoothResItem.setEnabled(false);
		
		HAPsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.H_APs();
			}
		});
		HAPsItem.setEnabled(false);
		
		/*******************************Help Menu******************************************/
		aboutItem.addActionListener(new ActionListener() {			//action listener for about
			public void actionPerformed(ActionEvent event) {
				aboutActionPerformed (event);
			}
		});

		return menuBarUpper ;
	}

	public static void loadtextActionPerformed (ActionEvent event)
	{
		String userhome = System.getProperty("user.dir");
		JFileChooser loadtextFC = new JFileChooser(userhome);
		loadtextFC.setMultiSelectionEnabled(true);
		loadtextFC.showOpenDialog(jsp);		//open the dialog box in a jscrollpane
		File[] files = loadtextFC.getSelectedFiles();
		
		panelCanvas.fillSample(files);
	}
	
	public static void newActionPerformed (ActionEvent event)
	{
		openImageAction();
	}
/*
	public static void openActionPerformed (ActionEvent event)
	{
		// ?? all unsaved progress will be lost, would you like to save before you open a new project?
		panelCanvas.initialize();
		openImageAction();
	}
*/
	public static void saveActionPerformed (ActionEvent event)
	{	
		try {
			SaveOptions dialog = new SaveOptions(panelCanvas, image);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void aboutActionPerformed (ActionEvent event)
	{
		JOptionPane.showMessageDialog(f, "WiMAP ver2.0\nÂ© Copyright, Jordan University of Science and Technology 2013.\n"
				+ "All rights reserved.\nNetwork Engineering and Security Department\n- Bayan Taani\n- Wala'a Adel\n"
				+ "- Deema Qusai", "About WiMAP", 3);
	}

	public static void openImageAction ()
	{
		String userhome = System.getProperty("user.dir");
		JFileChooser openFC = new JFileChooser(userhome);			//let the user browse for an image
		openFC.setAccessory(new ImagePreview(openFC));
		openFC.setAcceptAllFileFilterUsed(false);			// not all files are accepted
		openFC.addChoosableFileFilter(new ImageFilter());	//use the image filter class

		int fileToOpen = openFC.showOpenDialog(jsp);		//open the dialog box in a jscrollpane
		String openImagePath = null;									//to save the path of the selected image

		if (fileToOpen == JFileChooser.APPROVE_OPTION) {
			File file = openFC.getSelectedFile();
			openImagePath = file.getAbsolutePath();
		}

		try {
			image = ImageIO.read(new File(openImagePath));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		f.setTitle("WiMAP - " + openImagePath);

		panelCanvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));		//important for the scroll bars		
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelCanvas.openImage(image);

		/*Resize frame according to image*/
		if (image.getWidth() > f.getWidth())
			f.setExtendedState( f.getExtendedState()|JFrame.MAXIMIZED_HORIZ );
		else if (image.getWidth() > f.getWidth())
			f.setExtendedState( f.getExtendedState()|JFrame.MAXIMIZED_VERT );
		else
		{
			f.pack();
			f.setLocationRelativeTo(null);
		}
		panelCanvas.initialize();
		
	}
	
	/*Add the upper toolbar*/
	public void addToolBar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.HORIZONTAL);
		toolBar.setBackground(new Color(windowColor));
		toolBar.setFloatable(false);
		toolBar.setName("Tools");

		add(toolBar, BorderLayout.NORTH);

		toolBar.add(panelCanvas.addSamlpeCount());
		toolBar.add(panelCanvas.addRefreshBtn());
		toolBar.add(panelCanvas.addUndoBtn());		//add undo button to the tool bar
		toolBar.add(panelCanvas.addClearBtn());		//add clear button to the tool bar
		toolBar.add(panelCanvas.addSmoothBtn());	//add smooth button to the tool bar
		toolBar.add(panelCanvas.addDoneBtn());
	}


	/*Add the side panel that displays the scanned networks*/
	public void addSidePanel()
	{
		tb = new JTabbedPane();
		
		sidePanel = new JPanel(/*new BorderLayout()*/);
		sidePanel.setBackground(new Color(windowColor));
		sidePanel.setName("SidePanel");
		sidePanel.setPreferredSize(new Dimension(250, f.getHeight()));

		//sidePanel.add(new JLabel ("Available Networks:", SwingConstants.LEFT),  BorderLayout.PAGE_START);
		
		tb.setBackground(new Color(windowColor));
		tb.setPreferredSize(new Dimension(250, f.getHeight()));

		JButton scanBtn = new JButton("Live Scan");
		scanBtn.setToolTipText("Shows only result for last scan");
		scanBtn.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {

				for (int i = 0; i < av_net_panel.size(); i++)
				{//clear the side panel before any scan attempt
					sidePanel.remove(av_net_panel.get(i));
				}
				av_net_panel.clear();

				String command = "sh scan.sh";
				try {
					getRSSI.runShellScript(command);
				} catch (IOException e) {
					System.out.println("YOU ARE ON WINDOWS, AREN'T YOU?");//e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				getRSSI.readMyFile("result.txt");
				updateSidePanel(av_net_panel);
			}
		});

		sidePanel.add(scanBtn);

		sidePanel.setEnabled(true);
		sidePanel.setVisible(true);

		tb.addTab("Available Networks", sidePanel);
		tb.setEnabled(true);
		tb.setVisible(true);
		add(tb, BorderLayout.WEST);
		
		//adding the other tab:
		
		addAuthAPsTab();

		//add(sidePanel, BorderLayout.WEST);
	}
	
	public void updateSidePanel (ArrayList<JPanel> jPanelArr)
	{
		for (int i = 0; i < av_net_panel.size(); i++)
		{
			jPanelArr.get(i).setPreferredSize(new Dimension(sidePanel.getWidth(), 75));
			jPanelArr.get(i).validate();
			jPanelArr.get(i).repaint();
			sidePanel.add(jPanelArr.get(i));
			sidePanel.validate();
			sidePanel.repaint();
		}
	}

	private void addAuthAPsTab() {
		
		authAP_sp = new JPanel();
		
		authAP_sp.setBackground(new Color(windowColor));
		authAP_sp.setName("SidePanel");
		authAP_sp.setPreferredSize(new Dimension(240, f.getHeight()));
		updateAPList();
		
		authAP_sp.add(new JButton("Refresh"){{
		    addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            updateAPList();
		        }
		    });
		}});
		authAP_sp.setEnabled(true);
		authAP_sp.setVisible(true);
		JScrollPane APListSP = new JScrollPane(authAP_sp);
		tb.addTab("AP list", APListSP);
	}

	public static void updateAPList ()
	{
		for(int x = chbxlist.size(); x < PaintPane.Mac.size(); x++)
		{
			JPanel tempPanel = new JPanel();
			tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
			tempPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			tempPanel.setPreferredSize(new Dimension(sidePanel.getWidth(), 105));
			tempPanel.validate();
			tempPanel.repaint();

			chbxlist.add(new JCheckBox());
			tempPanel.add(chbxlist.get(chbxlist.size()-1));
			tempPanel.add(Box.createRigidArea(new Dimension(chbxlist.get(0).getWidth(), chbxlist.get(0).getHeight())));
			JLabel tempL = new JLabel("ESSID: " + PaintPane.Mac.get(x).getESSID());
			tempPanel.add(tempL);
			tempPanel.add(new JLabel("MAC: " + PaintPane.Mac.get(x).getMacAddress()));
			tempPanel.add(new JLabel("Channel: " + PaintPane.Mac.get(x).getChannel()));
			tempPanel.add(new JLabel("Position: (" + PaintPane.Mac.get(x).getApX() + "," + PaintPane.Mac.get(x).getApY() + ")"));
			tempPanel.add(new JLabel("Sample Count: " + PaintPane.Mac.get(x).getSampleCount()));

			authAP_sp.add(tempPanel);
			authAP_sp.validate();
			authAP_sp.repaint();
		}

	}
	public MainWindow() {
		panelCanvas = new PaintPane() ;

		sp = new JScrollPane(panelCanvas);
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);;
		addToolBar();
		addSidePanel();

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				f = new JFrame("WiMAP");
				JPanel p = new MainWindow();
				f.setExtendedState( f.getExtendedState()|JFrame.MAXIMIZED_BOTH );
				f.setContentPane(p);
				f.setJMenuBar(createMenuBar());
				f.setSize(400, 300);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}
