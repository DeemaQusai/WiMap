package gui;

import soft.getRSSI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

public class MainWindow extends JPanel {
	private static final long serialVersionUID = 1L;
	private static BufferedImage image;
	private static PaintPane panelCanvas;
	private static JScrollPane jsp;
	private static JPanel sidePanel;
	public static ArrayList<JPanel> av_net_panel = new ArrayList<JPanel> ();

	static JFrame f ;
	private static JScrollPane sp ;
	static int windowColor = 0xE6E6FA;

	public static JMenuBar createMenuBar()
	{
		JMenuBar menuBarUpper = new JMenuBar();		//define and initialized menuBarUpper
		menuBarUpper.setBackground(new Color(windowColor));
		f.setJMenuBar(menuBarUpper);			//add the menuBarUpper to the frame

		JMenu fileMenu = new JMenu("File");		//create the "file" menu
		JMenu toolsMenu = new JMenu("Tools");
		JMenu HelpMenu = new JMenu("Help");		//create "help" menu

		menuBarUpper.add(fileMenu);							//add the "file" menu to the bar
		menuBarUpper.add(toolsMenu);
		menuBarUpper.add(HelpMenu);							//add "help" menu to the bar

		JMenuItem newItem = new JMenuItem("New");
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
		
		JMenuItem openItem = new JMenuItem("Open");					//create the open
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
		
		final JMenuItem saveItem = new JMenuItem("Save");					//create the save
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		
		JMenuItem exit = new JMenuItem("Exit");						//create the exit
		
		JMenuItem computePLEItem = new JMenuItem("Compute PLE");
		JMenuItem insertAPItem = new JMenuItem("Insert AP");
		JMenuItem changeScaleItem = new JMenuItem("Change map scale");
		
		JMenuItem aboutItem = new JMenuItem("About WiMAP");			//create the about 

		newItem.setToolTipText("New project");		//tool tip (on mouse hover)
		openItem.setToolTipText("Open Floor Plan");		//tool tip (on mouse hover)
		saveItem.setToolTipText("Save Map");			//tool tip (on mouse hover)
		exit.setToolTipText("Exit application");		//tool tip (on mouse hover)

		fileMenu.add(newItem);			//add the open to the "file" menu
		fileMenu.add(openItem);			//add the open to the "file" menu
		fileMenu.add(saveItem);			//add the open to the "file" menu
		fileMenu.add(exit);				//add the exit to the "file" menu
		
		toolsMenu.add(computePLEItem);
		toolsMenu.add(insertAPItem);
		toolsMenu.add(changeScaleItem);
		
		HelpMenu.add(aboutItem);		//add the about to the "Help" menu
		
		computePLEItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.compute_n();
			}
		});
		
		insertAPItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelCanvas.apPosition();
			}
		});
		
		changeScaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				/******WORK HERE******/
				
			}
		});

		newItem.addActionListener(new ActionListener() {		//action listener for open
			public void actionPerformed(ActionEvent event) {
				boolean flag = true ;
				try {

					newActionPerformed (event);
				} catch (Exception e)
				{
					flag = false ;
				}
				saveItem.setEnabled(flag);
			}
		});

		openItem.addActionListener(new ActionListener() {		//action listener for open
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

		saveItem.addActionListener(new ActionListener() {		//action listener for open
			public void actionPerformed(ActionEvent event) {
				saveActionPerformed (event);
			}		
		});
		saveItem.setEnabled(false);

		exit.addActionListener(new ActionListener() {			//action listener for exit
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}

		});

		aboutItem.addActionListener(new ActionListener() {			//action listener for about
			public void actionPerformed(ActionEvent event) {
				aboutActionPerformed (event);
			}
		});

		return menuBarUpper ;
	}

	public static void newActionPerformed (ActionEvent event)
	{
		panelCanvas.initialize();
		openImageAction();
	}

	public static void openActionPerformed (ActionEvent event)
	{
		panelCanvas.initialize();
		openImageAction();
	}

	public static void saveActionPerformed (ActionEvent event)
	{	
		if(image == null)
			return ;
		JFileChooser saveFC = new JFileChooser();	//let the user browse for an image  
		saveFC.setAcceptAllFileFilterUsed(false);
		saveFC.addChoosableFileFilter(new ImageFilter());

		int fileToSave = saveFC.showSaveDialog(jsp);
		String saveImagePath = null;

		if (fileToSave == JFileChooser.APPROVE_OPTION) 
		{
			File file = saveFC.getSelectedFile();
			saveImagePath = file.getAbsolutePath();
			try {
				BufferedImage saving = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = saving.createGraphics();
				panelCanvas.paint(graphics);
				Graphics g = panelCanvas.getGraphics();
				g.dispose();
				File map = new File(saveImagePath+".png");		//there must be a way
				ImageIO.write(saving, "png", map);
			} catch(IOException exc) {
				System.out.println("problem saving");
			}catch (Exception e)
			{
				System.err.println(e.getMessage());

			}
		}
	}

	public static void aboutActionPerformed (ActionEvent event)
	{
		JOptionPane.showMessageDialog(f, "WiMAP ver2.0\n© Copyright, Jordan University of Science and Technology 2013.\n"
				+ "All rights reserved.\nNetwork Engineering and Security Department\n- Bayan Taani\n- Wala'a Adel\n"
				+ "- Deema Qusai", "About WiMAP", 3);
	}

	public static void openImageAction ()
	{
		String userhome = System.getProperty("user.dir");
		JFileChooser openFC = new JFileChooser(userhome);			//let the user browse for an image
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
		toolBar.add(panelCanvas.addUndoBtn());		//add undo button to the tool bar
		toolBar.add(panelCanvas.addClearBtn());		//add clear button to the tool bar
		toolBar.add(panelCanvas.addSmoothBtn());	//add smooth button to the tool bar
		toolBar.add(panelCanvas.addDoneBtn());
	}


	/*Add the side panel that displays the scanned networks*/
	public void addSidePanel()
	{
		sidePanel = new JPanel(/*new BorderLayout()*/);
		sidePanel.setBackground(new Color(windowColor));
		sidePanel.setName("SidePanel");
		sidePanel.setPreferredSize(new Dimension(250, f.getHeight()));

		sidePanel.add(new JLabel ("Available Networks:", SwingConstants.LEFT),  BorderLayout.PAGE_START);

		JButton scanBtn = new JButton("Scan");
		scanBtn.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {

				for (int i = 0; i < av_net_panel.size(); i++)
				{//clear the side panel before any scan attempt
					sidePanel.remove(av_net_panel.get(i));
				}
				av_net_panel.clear();

				/*	String command = "sh scan.sh";
				try {
					getRSSI.runShellScript(command);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				getRSSI.readMyFile("result.txt");
				for (int i = 0; i < av_net_panel.size(); i++)
				{
					av_net_panel.get(i).setPreferredSize(new Dimension(sidePanel.getWidth(), 60));
					av_net_panel.get(i).validate();
					av_net_panel.get(i).repaint();
					sidePanel.add(av_net_panel.get(i));
					sidePanel.validate();
					sidePanel.repaint();
				}
			}
		});

		sidePanel.add(scanBtn);

		sidePanel.setEnabled(true);
		sidePanel.setVisible(true);

		add(sidePanel, BorderLayout.WEST);
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
