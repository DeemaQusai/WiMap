package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import soft.*;

public class PaintPane extends JComponent {

	private static final long serialVersionUID = 1L;
	public static ArrayList<XY_sample> mySamples = null;
	static ArrayList<JLabel> myLabels = null;
	public static ArrayList <MAC_samples> Mac = new ArrayList<MAC_samples>();

	Random random = new Random ();
	int Max_index =0; //to save the index for AP which has maximum samples;
	
	public static ArrayList<MAC_samples> rogueAPs = new ArrayList<MAC_samples>();
	public static ArrayList<MAC_samples> authAPs = new ArrayList<MAC_samples>();

	private static final int DEFAULT_RES = 6;		// the default resolution for smoothing
	private JButton undoBtn ;
	private JButton clearBtn ;
	private JButton smoothBtn;
	private JButton doneBtn;
	private JButton refreshBtn;
	private JTextField sampleCount ;
	private JTextField MacCount;

	private int l = DEFAULT_RES ;
	private int rectLength = 15;		// for smoothing off

	public double mapScale ;
	public static int AP_x , AP_y;
	public double   PLE_n =0;			//path loss exponent
	private int clickCount = 0;
	private int x1 ,y1 ;
	private int x2 ,y2 ;
	private int virtual_Ap =0;   //set to 1 if we will use virtual AP
	private float P_d0;
	private double d0;
	private double Sigma =0;
	
	public static Boolean AP_here = false;

	//private static int starti ; // for finding the highest RSSI in mysamples <-- has ALL samples 
	//need for saving samples
	int i = 0; // # of distinct MAC address
	int n = 0; //Counter

	public boolean smoothOn = false;

	Timer timer ;
	BufferedImage image = null;
	BufferedImage APIcon = null;

	MouseListener samplingML ;		// mouse listener that handles taking samples
	MouseListener apPoML ;			// mouse listener for positioning an AP
	MouseListener scalingML ;		// mouse listener for scaling
	MouseListener Virtual_Samples;  //mouse listener for virtual APs

	public PaintPane ()
	{
		mySamples = new ArrayList<XY_sample>();
		myLabels = new ArrayList<JLabel>();
		setLayout(null);
		try
		{
			APIcon = ImageIO.read(new File("resources/AP.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		samplingML = new MouseAdapter() {		//on click executes this code
			@Override
			public void mouseClicked(MouseEvent e) {
				if(image == null)		// without it, if user clicks before inserting an image an exception is thrown (image.width & image.hight)
					return;

				if (e.getX() > image.getWidth() || e.getY() > image.getHeight())	//prevents user from taking samples outside of the image
					return;

				JLabel l = new JLabel ("Please wait while we take samples...", JLabel.CENTER);
				new Thread (new TimeOutOptionPane(MainWindow.f, l, "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, new Object[] {}, null)).start();
				
				
				/*************************WORK HERE************************/
			
				/*
				float RSSI = GetRSSI();

				sample s = new sample(RSSI, e.getX(), e.getY());	
				mySamples.add(s);
				JLabel tryLabel = new JLabel(Integer.toString(mySamples.size()+1));
				tryLabel.setToolTipText(Float.toString(s.getSignal()));	
				 */

				String show = "";
				Parser.parseMultiAP(e);

				JLabel tryLabel = new JLabel(Integer.toString(mySamples.size()+1));

				
				for(int n = 0 ; n < Parser.sigL.size(); n++)
				{
					if (n == 0)
						show = Float.toString(Parser.sigL.get(n));
					else
						show = show + "," + Float.toString(Parser.sigL.get(n));

				}
				tryLabel.setToolTipText(show);	
				 
				// handles the right click on a sample (a JLabel)
				tryLabel.addMouseListener(new MouseAdapter() {  
					public void mousePressed(MouseEvent mev){  

						if(mev.getButton() == MouseEvent.BUTTON3){				// if (right clicked)
							JLabel label = (JLabel) mev.getComponent();  		// return the sample (JLabel) you clicked on
							yesNo("Are you sure you want to delete this sample?", label);	// prompt the user and if yes delete the sample
						}
					}  

				});    

				tryLabel.setBounds(e.getX()-(10/2), e.getY()-(10/2), 10, 10);
				tryLabel.setVisible(true);
				myLabels.add(tryLabel);
				add(tryLabel);

				undoBtn.setEnabled(true);
				clearBtn.setEnabled(true);
				refreshBtn.setEnabled(true);
				smoothBtn.setEnabled(true);
				sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
				MacCount.setText("Number of AP's: " + Integer.toString(Mac.size()));
				repaint();
				System.out.println("Mac.size()" + Mac.size());
			}
		};	
		Virtual_Samples = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				double Distance;
				float RSSI;
				/*if(image == null)		// without it, if user clicks before inserting an image an exception is thrown (image.width & image.hight)
					return;

				if (e.getX() > image.getWidth() || e.getY() > image.getHeight())	//prevents user from taking samples outside of the image
					return;
				JLabel tryLabel = new JLabel(Integer.toString(MySamples+1));
				String show ="";
				for(int n =0 ; n < Parser.sigL.size(); n++)
				{
					if (n ==0)
						show = show  + Float.toString(Parser.sigL.get(n));
					else
						show = show + "," + Float.toString(Parser.sigL.get(n));

				}
				tryLabel.setToolTipText(show);	*/
				Distance = distance (e.getX() , e.getY() , (int) AP_x , (int) AP_y);
				PLE_n =3.5;
				for (int i = 0 ; i < Mac.size() ; i++)
				{
					Distance = distance (e.getX() , e.getY() , Mac.get(i).getApX() , Mac.get(i).getApY());
					RSSI = (float)(	P_d0 - (10* PLE_n * Math.log10(Distance/ d0)) * Find_Gaussian());
					sample s = new sample (RSSI ,e.getX() , e.getY(), Mac.get(i));
					Mac.get(i).addSample(s);

				}
				
				repaint();

			}

		};
		apPoML = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setAPpos(e.getX(), e.getY());
				JOptionPane.showMessageDialog(null, "The AP's location is: (" + AP_x + ","+ AP_y + ")");
				repaint();
				if (virtual_Ap ==1)
				{
					removeMouseListener(apPoML);
					removeMouseListener(samplingML);
					removeMouseListener(scalingML);
					addMouseListener(Virtual_Samples);
				}
				else
				{
					removeMouseListener(apPoML);
					addMouseListener(samplingML);//apPoFlag = false;
				}
				MainWindow.insertapdialog.setXY(e.getX(), e.getY());
				MainWindow.insertapdialog.setVisible(true);
			}
		};

		scalingML = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {

				if (image == null)
					return ;

				clickCount++ ;
				double pixd = 0;		// distance between the two pixels
				double reald = 0;		// the actual distance between the 2 points in meters

				if ( clickCount == 1)	// first click (first point)
				{
					x1 = ev.getX();
					y1 = ev.getY();
					//prompt user to click the second point
					JOptionPane.showMessageDialog(null, "Now, please click on second point" );
				}
				else if (clickCount == 2)	// second click
				{
					x2 = ev.getX();
					y2 = ev.getY();

					// now to calculate the scale
					pixd = distance(x2, y2, x1, y1);
					//System.out.println("click count = " + clickCount + "("+x1+","+y1+") ("+x2+","+y2+")");

					//prompt the user to enter the actual distance in meters
					try {
						String input = JOptionPane.showInputDialog(null, "Enter the Real distance (meter):", "");
						reald = Double.parseDouble(input);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					//					System.out.println("pixels " + pixd + " meters : " + reald);

					if (reald / pixd > 1)		// if actual distance > pixels distance
					{
						JOptionPane.showMessageDialog(null, "Map Scale is (meters : pixels)\n" + (reald/pixd)*10/10 + ":" + 1 );
						setScale(reald / pixd);	//??
					}
					else		// if pixels distance > actual distance
					{
						JOptionPane.showMessageDialog(null, "Map Scale is (meters : pixels)\n" + 1 + ":" + (pixd/reald)*10/10 );
						setScale(pixd / reald);//??
					}
					//System.out.println("Map scale : " + mapScale);

					clickCount = 0;
					removeMouseListener(scalingML);		// am done scaling
					addMouseListener(samplingML);		// start taking samples now
				}
				else{
					clickCount = 0;
				}
			}
		};

	}// end of constructor

	public void initialize ()
	{
		setLayout(null);
		removeMouseListener(samplingML);	// opened a new image so stop the sample taking
		addMouseListener(scalingML);		// need to take scale first.
		//clear();		// problem when loading data before image 
	}

	public void yesNo(String theMessage, JLabel label) {
		//prompt the user for confirmation to delete the sample
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response = JOptionPane.showConfirmDialog(null, theMessage, "",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (response == JOptionPane.YES_OPTION) {
			deleteSample(label);
		}
	}	

	public void openImage (BufferedImage im)
	{
		image = im ;
		this.setBounds(new Rectangle(im.getWidth(), im.getHeight()));
		repaint();

		if (l > image.getHeight() && l > image.getWidth())
			l = DEFAULT_RES; 		// if resolution is invalid restore default 
		//prompt user to scale
		JOptionPane.showMessageDialog(null, "Must determine the map scale before taking samples.\nclick on two points and enter the real distance between them in meters" );
		smoothBtn.setEnabled(true);
		doneBtn.setEnabled(true);
	}
/*
	public void repaintImage()
	{
		repaint();
	}
*/	
	public void paint (Graphics g)
	{
		MainWindow.updateAPList();
		super.paintComponent(g);		//don't know what it does...
		g.drawImage(image, 0, 0, null);		//draws the image at 0,0 every time 
		
		if (image == null)
			return ;
		
		//System.out.println("REPAINTING");
		//double value =0 ;
		int value =0 ;			// how bright the red is
		Color myColor;
		int image_H = 0;
		int image_W = 0;
		
		try{
			image_W = image.getHeight();
			image_H = image.getWidth();
		} catch (Exception e)
		{
			//throws exception when the image = null
		}

		if (smoothOn)
		{
			
			int p = 8;
			double d = 0;

			for(int x = 0; x<image_H ; x=x+l)
			{
				LabelHere:
					for (int y = 0; y<image_W ; y=y+l)
					{
						value = 0;
						double dx = 0 ;
						double wj = 0 ;
						d = 0;
						//starti = 0;
						for (int j = 0 ; j<mySamples.size() ; j++){ // = starti){
							d = distance(x, y, mySamples.get(j).getX(), mySamples.get(j).getY());
							if(x < mySamples.get(j).getX() && mySamples.get(j).getX() < x+l && y < mySamples.get(j).getY() && mySamples.get(j).getY() <y+l)
							{
								value = (int) Math.abs(mySamples.get(j).getRSSI());// getNextValue());
								//	System.out.println("value(" + x + "," + y + ") = " + value);

								myColor = getColor (Math.abs(value));

								g.setColor(myColor); 
								g.fillRect(x, y, l, l);		//
								continue LabelHere;
							}
							else
								wj = wj + 1/ (Math.pow(d, p)) ; 
						}
						dx = 0; 
						double wi = 0 ;
						//starti = 0;
						for(int i = 0 ; i<mySamples.size(); i++)// = starti)
						{
							dx = distance(x, y, mySamples.get(i).getX(), mySamples.get(i).getY()); 
							wi = 1 / Math.pow(dx, p) ;
							value = (int) (value + ((wi * (Math.abs(mySamples.get(i).getRSSI())))/wj));  //getNextValue()
						}

						myColor = getColor (Math.abs(value));
						//System.out.println("value(" + x + "," + y + ") = " + value);
						g.setColor(myColor); 
						g.fillRect(x, y, l, l);		//
					}
			//System.out.println("value(" + x + ") = " + value);
			}

		} 
		else {
			
			for (int i = 0; i < mySamples.size() ; i++)// = starti)
			{
				//value = (int)(mySamples.get(i).getSignal()) * -1;		//the strength of the red from the signal level
				value = (int) (mySamples.get(i).getRSSI()); //getNextValue() * -1);
				//System.out.println("VAL: " + value);
				//mySamples.get(i).printXYRSSI();
				myColor = getColor (Math.abs(value));
				g.setColor(myColor);
				//System.out.println(value);
				if (value > -1000)
					g.fillRect(mySamples.get(i).getX()-(rectLength/2), mySamples.get(i).getY()-(rectLength/2), rectLength, rectLength);		//
			}		
			
		}
		for (int i = 0 ; i < Mac.size() ; i++)
		{
			if (Mac.get(i).isLocated())
				g.drawImage(APIcon, Mac.get(i).getApX()-(APIcon.getWidth()/2), Mac.get(i).getApY()-(APIcon.getHeight()), null);
		}
		smoothOn = false;
	}

	public double distance (int x2, int y2, int x1, int y1)
	{
		double ds = 0; 
		double ret = 0 ;
		ds = (Math.pow(Math.abs((x2-x1)), 2) + Math.pow(Math.abs((y2-y1)), 2)) ;

		ret = Math.sqrt(ds) * 100 ;
		ret = Math.round(ret);
		ret = ret / 100 ;
		return ret;
	}

	public Color getColor (float val)
	{	
		float h = 0.0f; 	//Hue value (changes to give rainbow scale)
		float s = 0.9f;		//Saturation
		float b = 0.9f;		//Brightness

		for (int i = 20; i < val; i++){
			h+=0.006;		//increment hue with the decrease of the RSSI
		}
		int rgbColorCode = Color.HSBtoRGB(h, s, b);		//extract RGB color to add opacity
		String hex_string = "88" + Integer.toHexString(rgbColorCode).substring(2);
		Color rgb_color = new Color((int) Long.parseLong(hex_string, 16), true);

		//System.out.println("RSSI: " + val + " Color: " + rgb_color);

		return rgb_color;
		/*
		float h = 1.89f; 	//Hue value (changes to give rainbow scale)
		float s = 1.0f;		//Saturation
		float b = 0.6f;		//Brightness

		for (int i = 20; i < val; i++){
			b-=0.006;		//increment hue with the decrease of the RSSI
		}

		int rgbColorCode = Color.HSBtoRGB(h, s, b);		//extract RGB color to add opacity
		String hex_string = "88" + Integer.toHexString(rgbColorCode).substring(2);	//0x88 is the opacity code 
		Color rgb_color = new Color((int) Long.parseLong(hex_string, 16), true);

		//System.out.println("RSSI: " + val + " Color: " + rgb_color	);

		return rgb_color;*/
	}
	
	public float GetRSSI ()	
	{	// EXECUTE SCRIPT FILE HERE		
		//In case of 1 AP (the one we're connected to)
		double RSSI ;
		try {
			String unixCommand = "sh ws.sh";	//execute scan.sh in case of multi-AP scanning
			getRSSI.runShellScript(unixCommand);
			RSSI = getRSSI.readMyFile("signalLevel.txt"); 	//read result.txt in case of multi-AP scanning

		} catch (Exception e) {
			//RSSI = -1 * (Math.random() * 100) + 1;		//for testing purposes
			Random r = new Random();
			int Low = 20;
			int High = 150;
			RSSI = -1 * r.nextInt(High-Low) + Low;
			System.out.println("No file, will give random number as sample: " + RSSI);
		}

		return (float) RSSI;
	}

	public JTextField addSamlpeCount()
	{
		sampleCount = new JTextField("Number of samples: " + Integer.toString(mySamples.size()));
		sampleCount.setSize(20, HEIGHT);
		sampleCount.setEditable(false);
		//sampleCount.setHorizontalAlignment(JTextField.LEFT);
		return sampleCount;
	}

	public JTextField addMacCount()
	{
		MacCount = new JTextField("Number of AP's: " + Integer.toString(Mac.size()));
		MacCount.setSize(20, HEIGHT);
		MacCount.setEditable(false);
		//MacCount.setHorizontalAlignment(JTextField.LEFT);
		return MacCount;
	}
	
	//delete sample by right clicking on it
	public void deleteSample (JLabel label)
	{
		int index = myLabels.indexOf(label);
		JLabel rmlabel = myLabels.get(index);
		myLabels.remove(label);
		remove(rmlabel);
		for ( int i = 0; i < Mac.size() ; i++)
		{
			Mac.get(i).removeSample(mySamples.get(index).getX(), mySamples.get(index).getY());
		}
		mySamples.remove(index);
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));

		repaint();
	}

	//delete sample by index -> used in the undo button 
	public void deleteSample (int index)
	{
		JLabel rmlabel = myLabels.get(index);
		for ( int i = 0; i < Mac.size() ; i++)
		{
			Mac.get(i).removeSample(mySamples.get(index).getX(), mySamples.get(index).getY());
		}
		myLabels.remove(index);
		remove(rmlabel);
		mySamples.remove(index);
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));

		repaint();
	}

	public int setScale (double ms)
	{
		if (ms < 0)
			return -1;
		else
			mapScale = ms ;
		return 1;
	}

	public void setAPpos (int x , int y)
	{
		AP_x = x;
		AP_y = y;
	}

	public JButton addUndoBtn()
	{
		ImageIcon undoIcon = new ImageIcon("resources/undoIcon.png");
		Image img = undoIcon.getImage();  
		Image newimg = img.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);  
		undoIcon = new ImageIcon(newimg); 

		undoBtn = new JButton(undoIcon);
		undoBtn.setToolTipText("Undo");
		undoBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				undo();
			}
		});
		undoBtn.setEnabled(false);
		return undoBtn;
	}
	public void undo ()
	{
		if (!mySamples.isEmpty())
		{
			deleteSample(myLabels.size()-1);
		}
		if (mySamples.isEmpty())
		{
			undoBtn.setEnabled(false);
			clearBtn.setEnabled(false);
			smoothBtn.setEnabled(false);
			smoothOn = false ;
		}
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
	}
	public JButton addClearBtn()
	{
		ImageIcon clearIcon = new ImageIcon("resources/clearIcon.png");
		Image img = clearIcon.getImage();  
		Image newimg = img.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);  
		clearIcon = new ImageIcon(newimg); 

		clearBtn = new JButton(clearIcon);
		clearBtn.setToolTipText("Clear all data");
		clearBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				clear();
			}
		});
		clearBtn.setEnabled(false);
		return clearBtn;
	}
	public void clear ()
	{
		if (!mySamples.isEmpty())
		{
			mySamples.clear();
			myLabels.clear();
			removeAll();			//removes all components
			for (MAC_samples ap : Mac) {
				ap.clear();
			}
			undoBtn.setEnabled(false);
			clearBtn.setEnabled(false);
			smoothBtn.setEnabled(false);
			smoothOn = false ;
			repaint();
		}
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
	}
	public JButton addRefreshBtn()
	{
		ImageIcon refreshIcon = new ImageIcon("resources/refresh.png");
		Image img = refreshIcon.getImage();  
		Image newimg = img.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);  
		refreshIcon = new ImageIcon(newimg); 

		refreshBtn = new JButton(refreshIcon);
		refreshBtn.setToolTipText("refresh map");
		refreshBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				refresh();
			}
		});
		refreshBtn.setEnabled(false);
		return refreshBtn;
	}
	public void refresh()
	{
		for (int i = 0; i < mySamples.size(); i++)
			mySamples.get(i).changeMaxRSSI();
		repaint();
	}

	public JButton addSmoothBtn()
	{
		smoothBtn = new JButton("Smooth: On");
		smoothBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
/*				if(smoothOn)
				{	// if smooth button was on turn it off
					smoothOn = false;
					smoothBtn.setText("Smooth: On");
				}
				else
				{	// if smooth button was off turn it on
					smoothBtn.setText("Smooth: Off");
					smoothOn = true;
				}
*/				smoothOn = true;
				repaint();

			}
		});
		smoothBtn.setEnabled(false);
		return smoothBtn ;
	}

	public JButton addDoneBtn()
	{
		doneBtn = new JButton("Done");
		doneBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Done();
			}
		});
		doneBtn.setEnabled(false);
		return doneBtn;
	}

	public void Done()
	{
		/*
		 * On click, a new window will appear containing the list of 
		 * all scanned AP's. and the user will have the option to
		 * authorize some of them.
		 */

		ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox> ();

		JPanel p = new JPanel(new GridLayout(0,1));

		p.add(new JLabel("Please select authorized Access Points: "));

		for(int x = 0; x < Mac.size(); x++)
		{
			String text = "<html>"+Mac.get(x).getESSID()+"<br>"+Mac.get(x).getMacAddress()+"</html>";
			checkBoxList.add(new JCheckBox(text));
			p.add(checkBoxList.get(x));
		}

		JOptionPane.showMessageDialog(null,p);

		for(int x = 0; x < Mac.size(); x++)
		{
			if (!checkBoxList.get(x).isSelected())
			{
				String entry = checkBoxList.get(x).getText();
				int macindex = entry.indexOf("<br>", 0);
				String mac = entry.substring(macindex+4, entry.length()-7);	//to get the mac address from the string in the jcheckbox

				if (getEntryFromMACArray(mac) != null)
				{	
					rogueAPs.add(getEntryFromMACArray(mac));
				}
			}
		
		}
		authAPs();
		
		p.removeAll();
		p.add(new JLabel("Rogue Access Points:"));
		
		for (int x = 0; x < rogueAPs.size(); x++)
		{
			String text = "<html>"+rogueAPs.get(x).getESSID()+"<br>"+rogueAPs.get(x).getMacAddress()+"</html>";
			p.add(new JLabel(text));
		}
		
		JOptionPane.showMessageDialog(null, p);
		
		try {	
			// if there are no rogue APs it throw IndexOutOfBounds Exception
			EstimateAP.estimateAP(rogueAPs.get(0));
		}
		catch (Exception e)	{}
	
		if (Sigma == 0)
			Find_Sigma();
		//double guassianVar = Find_Gaussian();
		//System.out.println(guassianVar);
	}

	public void authAPs()
	{
		for (MAC_samples item : Mac)
		{
			if (!rogueAPs.contains(item))
			{
				authAPs.add(item);
			}
		}
	}
	
	/**
	 * Given a MAC address in string format, return the corresponding entry in the Mac list
	 */
	public static MAC_samples getEntryFromMACArray(String mac)
	{
		
		for (int i = 0; i < PaintPane.Mac.size(); i++)
		{
			if (mac.equals(PaintPane.Mac.get(i).getMacAddress()))
			{
				return PaintPane.Mac.get(i);
			}
			else
				continue;
		}
		return null;
	}

	public void apPosition()
	{
		JOptionPane.showMessageDialog(null, "Click on the location of the Access Point on the map");
		AP_here = true;		//flag to add AP icon in paint()
		this.removeMouseListener(samplingML);
		this.addMouseListener(apPoML);
	}

	public void compute_n ()
	{
		/*
				file.createNewFile();

				Writer = new BufferedWriter(new FileWriter(file));
				Writer.write("RSSI"+"," +"X"+","+"Y" );
				Writer.newLine();

				for(int i =0 ; i < mySamples.size(); i++)
				{
					Writer.write(mySamples.get(i).getSignal() +"," + mySamples.get(i).getX() + ","+mySamples.get(i).getY());
					Writer.newLine();

				}
				Writer.newLine();
				Writer.flush();
				Writer.close();
		 */
		ArrayList<Double> log_d = new ArrayList<Double>();
		Double distance = 0.0;
		int j =0 ;
		int max = Mac.get(0).getSampleCount();


		//To find AP which has largest number of samples***********
		for (int a =1 ; a < Mac.size(); a++)
		{
			if ( Mac.get(a).getSampleCount() > max)
			{
				max = Mac.get(a).getSampleCount();
				Max_index = a;
			}
		}
		/*****************************************************************************************/
		while ( j < Mac.get(Max_index).getSampleCount())
		{
			distance = Math.sqrt(Math.pow((Math.abs(Mac.get(Max_index).getS_X(j)-AP_x)),2) +  Math.pow((Math.abs(Mac.get(Max_index).getS_Y(j)-AP_y)),2))/mapScale;
			log_d.add(10*(Math.log10(distance))); 
			distance = 0.0;
			j++;
		} 

		// Computing slope 
		int s0 = 0;
		Double s1 = 0.0 , s2 = 0.0, t1 =0.0 , t2 = 0.0;
		Double M = 0.0 ;//, B = 0.0 ;

		for (int n=0 ; n < Mac.get(Max_index).getSampleCount() ; n++ )
		{
			s0++;
			if (Mac.get(Max_index).getS_X(n) == AP_x && Mac.get(Max_index).getS_Y(n) == AP_y)
			{
				t1 = t1+ Mac.get(Max_index).getS_RSSI(n);
				continue;
			}
			s1 = s1+ log_d.get(n);
			s2 = s2 + (log_d.get(n)* log_d.get(n));
			t1 = t1+ Mac.get(Max_index).getS_RSSI(n);
			t2 = t2 + (log_d.get(n) * Mac.get(Max_index).getS_RSSI(n));
		}

		M = ( s0*t2 - s1*t1 ) / (s0*s2 - s1*s1);
		//B = ( s2*t1 - s1*t2 ) / (s0*s2 - s1*s1);
		PLE_n = Math.abs(M);
		JOptionPane.showMessageDialog(null, "Path loss exponent (n) = "+ PLE_n );


		/*
		try {

			FileOutputStream fout= new FileOutputStream ("mac samples test");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(Parser.MAC_samples);
			fout.close();

			file.createNewFile();

			Writer = new BufferedWriter(new FileWriter(file));
			Writer.write("RSSI"+"," +"X"+","+"Y" );
			Writer.newLine();

			for(int i =0 ; i < mySamples.size(); i++)
			{
				Writer.write(mySamples.get(i).getSignal() +"," + mySamples.get(i).getX() + ","+mySamples.get(i).getY());
				Writer.newLine();

			}
			Writer.newLine();
			Writer.flush();
			Writer.close();

		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}

		ArrayList<Double> log_d = new ArrayList<Double>();
		Double distance = 0.0;
		int j =0 ;
		while ( j < mySamples.size())
		{
			distance = Math.sqrt(Math.pow((Math.abs(mySamples.get(j).getX()-AP_x)),2) +  Math.pow((Math.abs(mySamples.get(j).getY()-AP_y)),2))/40;
			log_d.add(10*(Math.log10(distance))); 
			distance = 0.0;
			j++;
		} 

		// Computing slope 
		int s0 = 0;
		Double s1 = 0.0 , s2 = 0.0, t1 =0.0 , t2 = 0.0;
		Double M = 0.0 ;//, B = 0.0 ;

		for (int n=0 ; n < mySamples.size() ; n++ )
		{
			s0++;
			if (mySamples.get(n).getX() == AP_x && mySamples.get(n).getY() == AP_y)
			{
				t1 = t1+ mySamples.get(n).getSignal();

				continue;
			}
			s1 = s1+ log_d.get(n);
			s2 = s2 + (log_d.get(n)* log_d.get(n));
			t1 = t1+ mySamples.get(n).getSignal();
			t2 = t2 + (log_d.get(n) * mySamples.get(n).getSignal());
		}

		M = ( s0*t2 - s1*t1 ) / (s0*s2 - s1*s1);
		//B = ( s2*t1 - s1*t2 ) / (s0*s2 - s1*s1);
		PLE_n = Math.abs(M);
		JOptionPane.showMessageDialog(null, "Path loss exponent (n) = "+ PLE_n );
		 */
	}

	public int getSmoothRes ()
	{
		return l;
	}

	public boolean setSmoothRes(int sr)
	{
		if(image != null)		// if the image is there check the limit to the resolution (the square's height)
		{
			if (sr < image.getHeight()&& sr < image.getWidth())	// if input is within limit
			{
				l = sr;
				rectLength = sr *(15/DEFAULT_RES);		// defailt for l = 6. default for rectlength = 20
				repaint();
				return true;
			}
			else			// input is invalid
				return false;
		}
		else				// there is no image to compare, must compare at opening image
		{
			l = sr;
			return true;
		}
	}

	public static void saveMacSamples(String path)
	{
		if (Mac.isEmpty())
		{
			System.out.println("MAC EMPTY");
			return ;
		}
		
		for (int i = 0; i < Mac.size(); i++)
		{
			PrintWriter writer ;		// to write each sample set on a file
			try {
				File file = new File(path + Mac.get(i).getMac_Address() +".csv");
				//File file = new File(path + "\\" + Mac.get(i).getMac_Address() +".csv");
				writer = new PrintWriter(file, "UTF-8");

				writer.println("ESSID," + ((Mac.get(i).getESSID()!=null)?Mac.get(i).getESSID():"null"));
				writer.println("Mac Address," + Mac.get(i).getMacAddress());
				writer.println("Channel," + Mac.get(i).getChannel());
				writer.println("AP position," + Mac.get(i).printAP_X_Y());
				writer.println("Authorization,"+(Mac.get(i).isAuthorized()?"Yes":"No"));
				writer.println("signal level,X-coordinate,Y-coordinate");	
				for (int j = 0; j < Mac.get(i).getSampleCount(); j++)
					writer.println(Mac.get(i).printSig_X_Y(j));

				writer.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void fillSample (File[] files)
	{
		Scanner scanner;
		try {

			for(int filenum = 0 ; filenum < files.length ; filenum++)
			{
				scanner = new Scanner(files[filenum]);
				String line;			
				String [] temp = new String[3];

				line = scanner.nextLine();		//	ESSID
				temp = line.split(",",3);
				String essid = temp[1];

				line = scanner.nextLine();		//	Mac Address
				temp = line.split(",",3);
				String macAdd = temp[1];

				line = scanner.nextLine();		//	Channel
				temp = line.split(",",3);
				int chnl = 0;
				try
				{
					chnl = Integer.parseInt(temp[1]);
				}
				catch(NumberFormatException nfe)
				{}

				line = scanner.nextLine();		//	AP position
				temp = line.split("," ,4);
				boolean located = false;
				int x = 0,y = 0;
				try
				{
					x = Integer.parseInt(temp[1]);
					y = Integer.parseInt(temp[2]);
					located = true;
				}
				catch(NumberFormatException nfe)
				{}

				line = scanner.nextLine();		//	Authorisation
				temp = line.split(",",3);
				Boolean auth ;
				if ("Yes".equalsIgnoreCase(temp[1]))
					auth = true;
				else
					auth = false;
				
				int i = 0;
				boolean found = false;
				for (i = 0 ;i < Mac.size() ;i++)
				{
					if (macAdd.equals(Mac.get(i).getMacAddress()))
					{
						found = true ;
						Mac.get(i).setAuthorized(auth);
						// MUST DO MORE STUFF HERE??
						break;
		
					}
				}
				if(Mac.isEmpty() || !found)
				{
					if (located)
						Mac.add(new MAC_samples(essid, macAdd, chnl, auth, x,y));
					else
						Mac.add(new MAC_samples(essid, macAdd, chnl, auth));
				}

				scanner.nextLine();		//skip the header of the file
				while (scanner.hasNextLine()) 
				{
					line = scanner.nextLine();
					temp = line.split(",");
					sample s = new sample(Float.parseFloat(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Mac.get(i));
					int k ;
					boolean foundIt = false;
					for (k = 0; k < mySamples.size() ; k++)
					{
						if (mySamples.get(k).getX() == s.getX() && mySamples.get(k).getY() == s.getY())
						{
							mySamples.get(k).addSample(s);
							foundIt = true;
						}
					}
					if (!foundIt && k == mySamples.size())
					{
						mySamples.add(new XY_sample(s.getX(), s.getY(), s));
					}
					Mac.get(i).addSample(s);
				}

				scanner.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		undoBtn.setEnabled(true);
		clearBtn.setEnabled(true);
		smoothBtn.setEnabled(true);
		refreshBtn.setEnabled(true);
		MacCount.setText("Number of AP's: " + Integer.toString(Mac.size()));
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
		repaint();

		
		for (int i = 0; i < mySamples.size() ; i++)
		{
			mySamples.get(i).printXYRSSI();
		}
		/*
		for (int i = 0 ; i < Mac.size(); i++)
		{
			System.out.println(Mac.get(i).getESSID());
			System.out.println(Mac.get(i).getMacAddress());
			System.out.println(Mac.get(i).getChannel());
			System.out.println("("+Mac.get(i).printAP_X_Y()+")");
			System.out.println(Mac.get(i).isAuthorized()?"YEAH":"NOPE");
			System.out.println();
		}
		*/
	}
	
	public void Find_Sigma ()
	{
		double X, Y , Result;
		double min =0;
		double J_n =0;
		//double sigma;
		int j=0;

		ArrayList <Double> distance_M = new ArrayList<Double>();
		ArrayList <Float> computed_P = new ArrayList<Float>();
		ArrayList <Float> calculated_P = new ArrayList<Float>();
		//System.out.println(Mac.get(Max_index).getSampleCount());


		//to calculate distance for each sample related to AP position (meters).
		for (int i =0; i < Mac.get(Max_index).getSampleCount() ; i++)
		{
			X = Math.abs(AP_x - Mac.get(Max_index).getS_X(i));
			Y = Math.abs(AP_y - Mac.get(Max_index).getS_Y(i));
			Result = Math.sqrt((Math.pow(X, 2) + Math.pow(Y, 2))) / mapScale;
			distance_M.add(Result);
			computed_P.add(Mac.get(Max_index).getS_RSSI(i));
			if(i == 0)
			{
				min = Result;
				j=i;
			}
			else
				if (Result < min)
				{
					min = Result;
					j=i;
				}
		}

		d0 = min;
		P_d0 = Mac.get(Max_index).getS_RSSI(j);

		//Calculate J(n) to Find sigmaa
		for (int n =0 ; n < distance_M.size() ; n++)
		{
			calculated_P.add((float)(0 - (10* PLE_n * Math.log10(distance_M.get(n)/ d0))));
			J_n = J_n + Math.pow( (computed_P.get(n) - calculated_P.get(n)) ,2);
		}
		Sigma = Math.sqrt(J_n / distance_M.size());
		System.out.println(Sigma);
		//Find_Gaussian (sigma);


		
	} //End Find_Sigma
	
	public void InsertSigma()
	{
		String input = JOptionPane.showInputDialog(null, "Enter the value of sigma which you prefer:", "");
		Sigma = Double.parseDouble(input);
	}
	public Double Find_Gaussian()
	{
	   return (random.nextGaussian() * Sigma);
	
	}

	public void H_APs ()
	{
		clear();
		virtual_Ap = 1;
		JOptionPane.showMessageDialog(null, "Please click on \"insert AP\" from Tools");	

	} // End H_Aps
	
}
	