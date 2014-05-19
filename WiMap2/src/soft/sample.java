package soft;

import java.util.Comparator;

public class sample implements Comparator<sample> {
	private float signal ;
	private int x;
	private int y;
	private MAC_samples myMac;
	//	private Boolean isAP;

	public sample()
	{
		signal = 0;
		x = 0 ;
		y = 0 ;
	}

	public sample(float s, int newx, int newy, MAC_samples mac)
	{
		signal = s;
		x = newx ;
		y = newy ;
		myMac = mac;
	}
	
	public sample(float s, int newx, int newy)
	{
		signal = s;
		x = newx ;
		y = newy ;
	}
	
	public void addMac(MAC_samples mac)
	{
		myMac = mac;
	}
	
	public boolean toPaint ()
	{
		return myMac.isRepresented();
	}
/*
	// just in case
	public sample(int newx, int newy)
	{
		signal = GetRSSI(newx, newy);
		x = newx ;
		y = newy ;
	}

 	
	public float GetRSSI (int x, int y)	{	// EXECUTE SCRIPT FILE HERE		
		//In case of 1 AP (the one we're connected to)		
		double RSSI ;
		try {
			String unixCommand = "sh ws.sh";
			getRSSI.runShellScript(unixCommand);
			RSSI = getRSSI.readMyFile("signalLevel.txt");
			
		} catch (Exception e) {

			try {
			    Thread.sleep(2500);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}

			Random r = new Random();
			int Low = 20;
			int High = 150;
			RSSI = -1 * (r.nextInt(High-Low) + Low);
			System.out.println("No file, will give random number as sample: " + RSSI);
		}
		
		return (float) RSSI;
	}
*/ 
	public float getSignal()
	{
		return signal ;
	}
	public int getX()
	{
		return x ;
	}
	public int getY()
	{
		return y ;
	}
	public float setSignal(float s)
	{
		signal = s ;
		return signal ;
	}
	public int setX(int newX)
	{
		x = newX ;
		return x ;
	}
	public int setY(int newY)
	{
		y = newY ;
		return y ;
	}

	// for the sort in an array
	public int compare(sample s1 ,sample s2) {
		if (s1.getSignal() < s2.getSignal()) return 1 ;
		if (s1.getSignal() > s2.getSignal()) return -1 ;
		return 0;

	}
	
	public boolean isAtSameLocation(sample s1)
	{
		if (x == s1.x && y == s1.y)
			return true;
		else
			return false;
	}
	
	public String toString ()
	{
		String temp =x+","+y + ":" +Float.toString(signal); 
		return temp;
	}

}