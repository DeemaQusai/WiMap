import java.awt.Color;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JFrame;


public class test extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) 
	{
	
		BufferedReader data_is = null;
		String record = null;
		String MAC = null;
		String RSSI = null;
		try {
			File f = new File("result2.txt");
			FileInputStream file_is = new FileInputStream(f);
			InputStreamReader buffered_is = new InputStreamReader(file_is);
			data_is = new BufferedReader(buffered_is); 

			while ( (record = data_is.readLine()) != null ) {
				
				//System.out.println(record);
				String[] feilds = record.split(" ");
				MAC = feilds[1];
				RSSI = feilds[2];
				
				System.out.println("MAC = " + MAC + " RSSI = " + RSSI);
				
			}

		} catch (IOException e) {
			// catch io errors from FileInputStream or readLine()
			System.out.println("Uh oh, got an IOException error!" + e.getMessage());

		} finally {
			// if the file opened okay, make sure we close it
			if (data_is != null) {
				try {
					data_is.close();
				} catch (IOException ioe) {
				}
			}
		}

		JCheckBox[] rb = new JCheckBox[15];
	    JPanel p = new JPanel(new GridLayout(0,1));

	    p.add(new JLabel("Please select authorized AP's: "));
	    
	    for(int x = 0; x < rb.length; x++)
	    {
	      rb[x] = new JCheckBox(""+x);
	      p.add(rb[x]);
	    }

	    JOptionPane.showMessageDialog(null,p);
	    System.exit(0);

		//new test();
	}

	public void paint (Graphics g)
	{
		ArrayList<int []> mySamples = new ArrayList<int []>() ;

		int a [][] = {
				{100, 250, 50},
				{350, 150, 50},
				{50, 50, 50},
				{250, 300, 50},
				{350, 450, 50}
		};

		mySamples.add(a[0]) ;
		mySamples.add(a[1]) ;
		mySamples.add(a[2]) ;
		mySamples.add(a[3]) ;
		mySamples.add(a[4]) ;

		int image_H = 500 ;
		int image_W = 500 ;
		int l = 5 ;
		int value =0 ;

		for(int x = 0; x<image_H ; x=x+l)
		{
			for (int y = 0; y<image_W ; y=y+l)
			{
				int p = 1;
				value = 0;
				//double ux = 0;
				double dx = 0 ;
				double wj = 0 ;
				for (int j = 0 ; j<mySamples.size() ; j++)
				{
					dx = dx + distance(x, y, mySamples.get(j)[0], mySamples.get(j)[1]) ; 
				}
				wj = 1 / (Math.pow((int)dx, p));
				dx = 0;
				double wi = 0 ;
				boolean f = false ;
				for(int i = 0 ; i<mySamples.size(); i++)
				{
					if(x < mySamples.get(i)[0] && mySamples.get(i)[0] < x+l && y < mySamples.get(i)[1] && mySamples.get(i)[1] <y+l)
					{
						Color myColor = getColor (mySamples.get(i)[2]);
						//System.out.println("value(" + x + "," + y + ") = " + value);
						g.setColor(myColor); 
						g.fillRect(x, y, l, l);		//
						f = true ;
						continue ;
					}
					dx = distance(x, y, mySamples.get(i)[0], mySamples.get(i)[1]); 

					wi = 1 / Math.pow((int)dx, p) ;
					System.out.println("(" + x + "," + y + ") " + mySamples.get(i)[2]+ " "+ wi /wj);

					value = value + (int)((wi * mySamples.get(i)[2])/wj);

				}
				//    			value = value * -1 ;
				if (!f){
					Color myColor = getColor (value/100);
					System.out.println("value(" + x + "," + y + ") = " + value);
					g.setColor(myColor); 
					g.fillRect(x, y, l, l);		//
					f = false ;
				}
			}
			//System.out.println("value(" + x + ") = " + value);
		}

	}
	public Color getColor (int val)
	{
		Color pix ;
		if (val < 15)
			pix = Color.BLACK ;
		else if (val < 30)
			pix = Color.BLUE ;
		else if (val < 45)
			pix = Color.GREEN ;
		else if (val < 60)
			pix = Color.YELLOW ;
		else if (val < 75)
			pix = Color.ORANGE ;
		else
			pix = Color.RED ;

		return pix ;

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

	public test()
	{
		this.setTitle("YO");
		this.setSize(500,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

}
