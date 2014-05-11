package soft;

import gui.PaintPane;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	
	public static ArrayList <Float> sigL= new ArrayList<Float>();
	
	//need for saving samples
		static int i = 0; // # of distinct MAC address
		static int n = 0; //Counter

	public static void parseMultiAP(MouseEvent e)
	{
		sigL.clear();
		String S = "";
		String MACAdd = "";
		String essid = "";
		int channel = 0 ;
		float RSSI = 0;
		sample s = null ;
		BufferedReader br = null;
		String[] temp ;
		
		// IS this right here?
		String command = "sh scan.sh";
		try {
			getRSSI.runShellScript(command);
		} catch (IOException ioe) {
			System.out.println("YOU ARE ON WINDOWS, AREN'T YOU?");//e.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		
		try {
			br = new BufferedReader(new FileReader("result.txt")); 

			boolean found = false ;
			int foundAt = PaintPane.Mac.size();

			while ((S = br.readLine()) != null) {
				//Read MAC address line								
				if (S.contains("Cell ") && !S.contains("Cell 01"))
				{
					if (!found)
						PaintPane.Mac.add(new MAC_samples(essid, MACAdd.trim(), channel, s));
					else
						PaintPane.Mac.get(foundAt).addSample(s);

					MACAdd = "" ;
					essid = "";
					channel = 0 ;
					found = false ;
					foundAt = PaintPane.Mac.size();		
				}
				if(S.contains("Address:"))
				{
					temp = S.split("Address:", 2);
					MACAdd = temp[1];
					for (int i = 0 ; i < PaintPane.Mac.size() ; i++)
					{
						if (MACAdd.trim().equals(PaintPane.Mac.get(i).getMacAddress()))
						{
							found = true ;
							foundAt = i ;
							break;
						}
					}
				}

				else if (S.contains("ESSID")) {
					temp = S.split("ESSID:\"",2);
					temp = temp[1].split("\"",2);
					essid  = temp[0];
				}else if (S.trim().startsWith("Channel"))
				{
					temp = S.split("Channel:",2);
					channel = Integer.parseInt(temp[1]);
				}
				else if(S.contains("Signal level"))
				{ 
					temp = S.split("Signal level=", 2);
					temp = temp[1].split(" ",2);
					RSSI = Float.parseFloat(temp[0]);
					//sample s = new sample(RSSI, e.getX(), e.getY());
					s = new sample(RSSI, e.getX(), e.getY());
					sigL.add(RSSI);
				}
				
			}
			if (!found)
				PaintPane.Mac.add(new MAC_samples(essid, MACAdd.trim(), channel, s));
			else
				PaintPane.Mac.get(foundAt).addSample(s);

		} catch (FileNotFoundException k) {
			k.printStackTrace();
		} catch (IOException t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
		
		Float max = sigL.get(0); 
		for (int i = 1; i < sigL.size() ; i++)
		{
			if (max < sigL.get(i))
				max = sigL.get(i);
		}
		PaintPane.mySamples.add(new sample(max, e.getX(), e.getY()));
		System.out.println("mysample.size()" + PaintPane.mySamples.size());
		
		
		/*
		for (int i = 0 ; i < PaintPane.mySamples.size() ; i++)
		{
			System.out.println(PaintPane.mySamples.get(i).getX() + "," + PaintPane.mySamples.get(i).getY() + " : " + PaintPane.mySamples.get(i).getSignal() );
		}*/
		//sample D = new sample(RSSI , e.getX(), e.getY()); // to save all samples ( not for specific MAC address)
		//PaintPane.mySamples.add(D);
	}
}
