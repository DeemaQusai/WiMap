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
		float RSSI = 0;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader("result.txt")); 
		} catch (FileNotFoundException k) {
			k.printStackTrace();
		}
		try {
			while ((S = br.readLine()) != null) {
				//Read MAC address line
				if(S.startsWith("Address:"))
				{
					MACAdd = S.substring(9);
					if (PaintPane.Mac.isEmpty())
					{
						MAC_samples M = new MAC_samples(MACAdd);
						PaintPane.Mac.add(M);
						i++;
					}
					else
					{
						while (n < i)
						{
							if (MACAdd.equals(PaintPane.Mac.get(n).getMacAddress()))
								break;
							else
								n++;
						}

						if(n == i )
						{
							MAC_samples M = new MAC_samples(MACAdd);
							PaintPane.Mac.add(M);
							i++;
						}
					}
				}else if (S.startsWith("ESSID")) {
					essid  = S.substring(6);
					PaintPane.Mac.get(n).setESSID(essid);

				}
				//Read RSSI
				if(S.startsWith("RSSI"))
				{ 
					RSSI = Float.parseFloat(S.substring(6));
					//sample s = new sample(RSSI, e.getX(), e.getY());
					PaintPane.Mac.get(n).addSample(RSSI, e.getX(), e.getY());
					sigL.add(RSSI);
					n=0;
					MACAdd = "";
				}
			}
		} catch (IOException t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
		
		sample D = new sample(RSSI , e.getX(), e.getY()); // to save all samples ( not for specific MAC address)
		PaintPane.mySamples.add(D);
	}
}
