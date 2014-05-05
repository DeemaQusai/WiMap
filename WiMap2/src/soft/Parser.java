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
			while ((S = br.readLine()) != null) {
				//Read MAC address line
				if(S.contains("Address:"))
				{
					temp = S.split("Address:", 2);
					MACAdd = temp[1];
					if (PaintPane.Mac.isEmpty())
					{
						MAC_samples M = new MAC_samples(MACAdd.trim());
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
							MAC_samples M = new MAC_samples(MACAdd.trim());
							PaintPane.Mac.add(M);
							i++;
						}
					}
				}else if (S.contains("ESSID")) {
					temp = S.split("ESSID:\"",2);
					temp = temp[1].split("\"",2);
					essid  = temp[0];
					PaintPane.Mac.get(n).setESSID(essid);
				}else if (S.trim().startsWith("Channel"))
				{
					temp = S.split("Channel:",2);
					channel = Integer.parseInt(temp[1]);
					PaintPane.Mac.get(n).setChannel(channel);
				}
				
				//Read RSSI
				else if(S.contains("RSSI"))
				{ 
					temp = S.split("Signal level=", 2);
					RSSI = Float.parseFloat(temp[1]);
					//sample s = new sample(RSSI, e.getX(), e.getY());
					PaintPane.Mac.get(n).addSample(RSSI, e.getX(), e.getY());
					sigL.add(RSSI);
					n=0;
					MACAdd = "";
				}
				
			}
		} catch (FileNotFoundException k) {
			k.printStackTrace();
		} catch (IOException t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
		
		sample D = new sample(RSSI , e.getX(), e.getY()); // to save all samples ( not for specific MAC address)
		PaintPane.mySamples.add(D);
	}
}
