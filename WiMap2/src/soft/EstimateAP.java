package soft;

import gui.*;
import java.awt.Color;
import java.awt.Graphics;

public class EstimateAP {

	public static void estimateAP(MAC_samples ap_readings)
	{
		double Max = 0;
		double sum1 = 0;
		double sum2 =0;
		double X,Y;
		Graphics g = MainWindow.panelCanvas.getGraphics();
		
		if (ap_readings.getSampleCount() < 10)
		{
			//the Maximum signal level
			X= ap_readings.getS_X(0);//getSampleArr().get(0).getX();
			Y= ap_readings.getS_Y(0);//getSampleArr().get(0).getY();

			//draw circle around the AP (Estimation) 
			g.setColor(Color.BLACK); 
			g.drawOval((int)(X-60), (int)(Y-60), 120 , 120);
			
		} else if (ap_readings.getSampleCount() >= 10)
		{
			Max = (int)(0.2 * ap_readings.getSampleCount()); 

			for ( int j = 0  ; j < Max; j++)
			{ 
				sum1 = sum1 + ap_readings.getS_X(j);//getSampleArr().get(j).getX();
				sum2 = sum2 + ap_readings.getS_Y(j);//.getSampleArr().get(j).getY();
			}


			//calculate average of the X and Y depend on number of Maximum samples...
			X = sum1/(Max); 
			Y = sum2/(Max);

			//draw circle around the AP (Estimation)
			g.setColor(Color.BLACK); 
			g.drawOval((int)(X-60), (int)(Y-60), 120 , 120);
		}
	}
}
