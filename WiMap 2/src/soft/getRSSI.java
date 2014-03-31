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
				String line = "";

				JLabel addrLabel = new JLabel();
				JLabel essidLabel = new JLabel();
				JLabel rssiLabel = new JLabel();
				Color color = new Color(0xCC0000);
				Color clBrighter = new Color (0);
				
				while ( (record=data_is.readLine()) != null ) {

					color = new Color(0xCC0000);

					if (record.startsWith("Address")) {
						line = record.substring(9);
						addrLabel = new JLabel("MAC: " + line);

					} else if (record.startsWith("ESSID")) {
						line  = record.substring(6);
						essidLabel = new JLabel("ESSID: " + line);

					} else if (record.startsWith("RSSI")) {
						line  = record.substring(5);
						rssiLabel = new JLabel("Signal Level: " + line + " dBm");
						double level = Double.parseDouble(line);

						for (int i = 0; i > level; i--){
							clBrighter = Blend(color, Color.white, (float) 0.98);	//add whiteness according to the RSSI
							color = clBrighter;
						}
						JPanel tempPanel = new JPanel();
						tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
						tempPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
						tempPanel.setBackground(clBrighter);

						tempPanel.add(essidLabel);
						tempPanel.add(addrLabel);
						tempPanel.add(rssiLabel);

						MainWindow.av_net_panel.add(tempPanel);

					}
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
			System.out.println(consoleDisplay);
		}
		try {
			inputStream.close();
		} catch (IOException iOException) { }
	}
}