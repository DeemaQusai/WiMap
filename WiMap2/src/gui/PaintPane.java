package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import soft.getRSSI;
import soft.sample;
 
public class PaintPane extends JComponent {
 
	private static final long serialVersionUID = 1L;
	static ArrayList<sample> mySamples = null;
	static ArrayList<JLabel> myLabels = null;
	private File file = new File("Mysamples_result.csv");
	private BufferedWriter Writer;
 
	private JButton undoBtn ;
	private JButton clearBtn ;
	private JButton NBtn;
	private JButton apPositionBtn ;
	private JButton smoothBtn;
	private JTextField sampleCount ;
 
	public double mapScale ;
	public double AP_x , AP_y;
	public double   PLE_n =0;			//path loss exponent
	private int clickCount = 0;
	private int x1 ,y1 ;
	private int x2 ,y2 ;

	public boolean smoothOn = false;
 
	Timer timer ;
	BufferedImage image = null;

	MouseListener samplingML ;		// mouse listener that handles taking samples
	MouseListener apPoML ;			// mouse listener for positioning an AP
	MouseListener scalingML ;		// mouse listener for scaling
 
	public PaintPane ()
	{
		mySamples = new ArrayList<sample>();
		myLabels = new ArrayList<JLabel>();
		setLayout(null);
		
		samplingML = new MouseAdapter() {		//on click executes this code
			@Override
			public void mouseClicked(MouseEvent e) {
				if(image == null)		// without it, if user clicks before inserting an image an exception is thrown (image.width & image.hight)
					return;

				if (e.getX() > image.getWidth() || e.getY() > image.getHeight())	//prevents user from taking samples outside of the image
					return;

//				JOptionPane.showMessageDialog(null, "These are your coordinates ("+e.getX()+","+e.getY()+").\nClick OK and wait 5 seconds.");
				TimeOutOptionPane countdown_msg = new TimeOutOptionPane();
				countdown_msg.showTimeoutDialog(MainWindow.f, "Please wait while we take samples...", "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, new Object[] {}, null);
				 					
				float RSSI = GetRSSI();
				 
				sample s = new sample(RSSI, e.getX(), e.getY());			//change here
//				sample s = new sample(e.getX(), e.getY());			
				mySamples.add(s);

				JLabel tryLabel = new JLabel(Integer.toString(mySamples.size()+1));
				tryLabel.setToolTipText(Float.toString(s.getSignal()));		

				// handles the right click on a sample (a JLabel)
				tryLabel.addMouseListener(new MouseAdapter() {  
					public void mousePressed(MouseEvent mev){  

						if(mev.getButton() == MouseEvent.BUTTON3){				// if (right clicked)
							JLabel label = (JLabel) mev.getComponent();  		// return the sample (JLabel) you clicked on
							yesNo("Are you sure you want to delete this sample?", label);	// prompt the user and if yes delete the sample
						}
					}  

				});    

				tryLabel.setBounds(e.getX()-15, e.getY()-15, 30, 30);
				tryLabel.setVisible(true);
				myLabels.add(tryLabel);
				add(tryLabel);

				undoBtn.setEnabled(true);
				clearBtn.setEnabled(true);
				smoothBtn.setEnabled(true);
				sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
				repaint();
				
			}
		};	
		
		apPoML = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setAPpos(e.getX(), e.getY());
				JOptionPane.showMessageDialog(null, "The AP's location is: (" + AP_x + ","+ AP_y + ")");
				NBtn.setEnabled(true);		//HERE check for the number of samples taken???	
				removeMouseListener(apPoML);
				addMouseListener(samplingML);//apPoFlag = false;
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
					System.out.println("click count = " + clickCount + "("+x1+","+y1+") ("+x2+","+y2+")");
					
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
					System.out.println("Map scale : " + mapScale);
					
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
		this.removeMouseListener(samplingML);	// opened a new image so stop the sample taking
		this.addMouseListener(scalingML);		// need to take scale first.
		clear();
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
		apPositionBtn.setEnabled(true);
		smoothBtn.setEnabled(true);
		
		//prompt user to scale
		JOptionPane.showMessageDialog(null, "Must determine the map scale before taking samples.\n click on two points and enter the real distance between them in meters" );

	}
 
	public void paint (Graphics g)
	{
		super.paintComponent(g);		//don't know what it does...
		g.drawImage(image, 0, 0, null);		//draws the image at 0,0 every time 
 
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
			int l =  6;
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
					for (int j = 0 ; j<mySamples.size() ; j++){
						d = distance(x, y, (int)mySamples.get(j).getX(), (int)mySamples.get(j).getY());
						if(x < mySamples.get(j).getX() && mySamples.get(j).getX() < x+l && y < mySamples.get(j).getY() && mySamples.get(j).getY() <y+l)
						{
							value = (int) Math.abs(mySamples.get(j).getSignal());
						//	System.out.println("value(" + x + "," + y + ") = " + value);
							
							myColor = getColor (value);
							
							g.setColor(myColor); 
							g.fillRect(x, y, l, l);		//
							continue LabelHere;
						}
						else
							wj = wj + 1/ (Math.pow(d, p)) ; 
					}
					dx = 0;
					double wi = 0 ;
					for(int i = 0 ; i<mySamples.size(); i++)
					{
						dx = distance(x, y, (int)mySamples.get(i).getX(), (int)mySamples.get(i).getY()); 
						wi = 1 / Math.pow(dx, p) ;
						value = (int) (value + ((wi * (Math.abs(mySamples.get(i).getSignal())))/wj));
					}
 
						myColor = getColor (value);
						//System.out.println("value(" + x + "," + y + ") = " + value);
						g.setColor(myColor); 
						g.fillRect(x, y, l, l);		//
				}
				//System.out.println("value(" + x + ") = " + value);
			}
 
		}
		else {
			for (int i=0; i<mySamples.size() ; i++)
			{
				value = (int)(mySamples.get(i).getSignal()) * -1;		//the strength of the red from the signal level
				//System.out.println(value);			//test
				//myColor = new Color(255, value,value, 200);		//WHAT IS 128????
				myColor = getColor (value);
				g.setColor(myColor); 
				g.fillRect(mySamples.get(i).getX()-10, mySamples.get(i).getY()-10, 20, 20);		//
			}					
		}
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
 
	public Color getColor (double val)
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
	}

	public float GetRSSI ()	{	// EXECUTE SCRIPT FILE HERE		
		//In case of 1 AP (the one we're connected to)
		double RSSI ;
		try {
			String unixCommand = "sh ws.sh";
			getRSSI.runShellScript(unixCommand);
			RSSI = getRSSI.readMyFile("signalLevel.txt");
			
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
		sampleCount.setSize(WIDTH, HEIGHT);
		sampleCount.setEditable(false);
		sampleCount.setHorizontalAlignment(JTextField.LEFT);
		return sampleCount;
	}
 
	//delete sample by right clicking on it
	public void deleteSample (JLabel label)
	{
		int index = myLabels.indexOf(label);
		JLabel rmlabel = myLabels.get(index);
		myLabels.remove(label);
		remove(rmlabel);
		mySamples.remove(index);
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
 
		repaint();
	}
 
	//delete sample by index -> used in the undo button 
	public void deleteSample (int index)
	{
		JLabel rmlabel = myLabels.get(index-1);
		myLabels.remove(index-1);
		remove(rmlabel);
		mySamples.remove(index-1);
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
 
	public void setAPpos (double x , double y)
	{
		AP_x = x;
		AP_y =y;
	}
 
	public JButton addUndoBtn()
	{
		undoBtn = new JButton("Undo");
		undoBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				undo();
				sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
			}
		});
		undoBtn.setEnabled(false);
		return undoBtn;
	}
	public void undo ()
	{
		if (!mySamples.isEmpty())
		{
			deleteSample(myLabels.size());
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
		clearBtn = new JButton("Clear");
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
			undoBtn.setEnabled(false);
			clearBtn.setEnabled(false);
			smoothBtn.setEnabled(false);
			smoothOn = false ;
			repaint();
		}
		sampleCount.setText("Number of samples: " + Integer.toString(mySamples.size()));
	}
	public JButton addSmoothBtn()
	{
		smoothBtn = new JButton("Smooth: On");
		smoothBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(smoothOn)
				{	// if smooth button was on turn it off
					smoothOn = false;
					smoothBtn.setText("Smooth: On");
				}
				else
				{	// if smooth button was off turn it on
					smoothBtn.setText("Smooth: Off");
					smoothOn = true;
				}
				repaint();
			}
		});
		smoothBtn.setEnabled(false);
		return smoothBtn ;
	}
 	public JButton addApPositionBtn()
	{
		apPositionBtn = new JButton("Inseret AP location");
		apPositionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				apPosition();
			}
		});
		apPositionBtn.setEnabled(false);
		return apPositionBtn;
	}
 	public void apPosition()
	{
		JOptionPane.showMessageDialog(null, "Click on the location of the Access Point on the map");
		this.removeMouseListener(samplingML);
		this.addMouseListener(apPoML);
	}
 	public JButton addNBtn()
	{
		NBtn = new JButton("Compute n");
		NBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				compute_n();
			}
		});
		NBtn.setEnabled(false);
		return NBtn;
	}
 	public void compute_n ()
	{
		try {
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
			distance = Math.sqrt(Math.pow((Math.abs(mySamples.get(j).getX()-AP_x)),2) +  Math.pow((Math.abs(mySamples.get(j).getY()-AP_y)),2))/40;	// ??
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
	}
}	// end of class

