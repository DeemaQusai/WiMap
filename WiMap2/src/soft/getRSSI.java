package soft;

import gui.MainWindow;

import java.awt.Color;
import java.io.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class getRSSI {
	
	public static String RSSI = "";
	public static String MAC = "";

	//function that reads the average signal stored in the file after the shell script is executed
	/**
	 * @wbp.parser.entryPoint
	 */
	public static double readMyFile(String fileName) {
		
		if (fileName == "result.txt") {

			BufferedReader data_is = null;
			String record = null;
			try {
				File f = new File(fileName);
				FileInputStream file_is = new FileInputStream(f);
				InputStreamReader buffered_is = new InputStreamReader(file_is);
				data_is = new BufferedReader(buffered_is); 

				JLabel addrLabel = new JLabel();
				JLabel chanlLabel = new JLabel();
				JLabel rssiLabel = new JLabel();
				Color color = new Color(0xCC0000);
				Color clBrighter = new Color (0);
				String[] temp ; //for parsing (.split)
				String ESSID = null;
				JPanel tempPanel =null;
				while ( (record=data_is.readLine()) != null ) {

					color = new Color(0xCC0000);
					double level= 0;
					if (record.contains("Cell ") && !record.contains("Cell 01"))
					{
						tempPanel = new JPanel();
						tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
						tempPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
						tempPanel.setBackground(clBrighter);

						tempPanel.add(new JLabel("ESSID: " + ESSID));
						tempPanel.add(addrLabel);
						tempPanel.add(chanlLabel);
						tempPanel.add(rssiLabel);
						ESSID = null ;
						MainWindow.av_net_panel.add(tempPanel);
					}
					if (record.contains("Address")) {
						temp = record.split("Address:",2);
						addrLabel = new JLabel("MAC: " + temp[1]);

					} else if (record.contains("ESSID")) {
						temp = record.split("ESSID:\"",2);
						temp = temp[1].split("\"",2);
						ESSID = temp[0];
//						essidLabel = new JLabel("ESSID: " + temp[0]);

					} else if (record.trim().startsWith("Channel")) {
						temp = record.split("Channel:");
						chanlLabel = new JLabel("Channel: " + temp[1]);
						
					} else if (record.contains("Signal level=")) {
						temp = record.split("Signal level=", 2);
						temp = temp[1].split(" ",2);
						rssiLabel = new JLabel("RSSI: " + temp[0]+ " dBm");
						level = Double.parseDouble(temp[0]);
						for (int i = 0; i > level; i--){
							clBrighter = Blend(color, Color.white, (float) 0.98);	//add whiteness according to the RSSI
							color = clBrighter;
						}
					}
				}
				tempPanel = new JPanel();
				tempPanel.setBackground(clBrighter);

				tempPanel.add(new JLabel("ESSID: " + ESSID));
				tempPanel.add(addrLabel);
				tempPanel.add(chanlLabel);
				tempPanel.add(rssiLabel);
				tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
				tempPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

				MainWindow.av_net_panel.add(tempPanel);

				data_is.close();
			} catch (IOException e) {
				// catch io errors from FileInputStream or readLine()
				e.printStackTrace();
//				System.out.println("Uh oh, got an IOException error!" + e.getMessage());

			} finally {
				// if the file opened okay, make sure we close it
				if (data_is != null) {
					try {
						data_is.close();
					} catch (IOException ioe) {
					}
				}
			}

		} else if (fileName == "result2.txt") {

			/*
			 * In this file, the format is as following:
			 * MAC1 RSSI1
			 * MAC2 RSSI2
			 * .
			 * .
			 * So the parsing should take the first field as MAC address and the second as the RSSI*/

			BufferedReader data_is = null;
			String record = null;

			try {
				File f = new File(fileName);
				FileInputStream file_is = new FileInputStream(f);
				InputStreamReader buffered_is = new InputStreamReader(file_is);
				data_is = new BufferedReader(buffered_is); 

				while ( (record = data_is.readLine()) != null ) {
					
					String[] feilds = record.split(" ");
					MAC = feilds[1];
					RSSI = feilds[2];
					
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


		} else if (fileName == "signalLevel.txt") {
			BufferedReader data_is = null;
			String record = null;
			try {
				File f = new File(fileName);
				FileInputStream file_is = new FileInputStream(f);
				InputStreamReader buffered_is = new InputStreamReader(file_is);
				data_is = new BufferedReader(buffered_is); 

				while ( (record=data_is.readLine()) != null ) {
					return Double.parseDouble(record);
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
		}
		return -1;
	}

	static Color Blend(Color clOne, Color clTwo, float fAmount) {
		float fInverse = (float) (1.0 - fAmount);

		// I had to look up getting color components in java.  Google is good :)
		float afOne[] = new float[3];
		clOne.getColorComponents(afOne);
		float afTwo[] = new float[3]; 
		clTwo.getColorComponents(afTwo);    

		float afResult[] = new float[3];
		afResult[0] = afOne[0] * fAmount + afTwo[0] * fInverse;
		afResult[1] = afOne[1] * fAmount + afTwo[1] * fInverse;
		afResult[2] = afOne[2] * fAmount + afTwo[2] * fInverse;

		return new Color (afResult[0], afResult[1], afResult[2]);
	}

	//function to execute the shell script
	public static void runShellScript(String unixCommand) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", unixCommand);
		processBuilder.redirectErrorStream(true); 
		Process shellProcess = processBuilder.start();
		InputStream inputStream = shellProcess.getInputStream(); 
		int consoleDisplay;
		while((consoleDisplay=inputStream.read())!=-1) {
			//System.out.println(consoleDisplay);
		}
		try {
			inputStream.close();
		} catch (IOException iOException) { }
	}
}