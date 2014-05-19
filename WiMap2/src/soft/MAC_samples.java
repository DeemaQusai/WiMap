package soft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class MAC_samples
{
	double RSSI;
	int X , Y;
	private boolean isAuth ;
	private boolean isLocated = false;
	private int channel ;
	private boolean isRepresented = true;	// for drawing
	private String MacAddress;
	private String Essid;
	private ArrayList <sample> sampleArr = new ArrayList <sample> (); //save samples for each Mac address

	/*
	 * you can't create a MAC_samples object without having it's Mac address
	 */
	
	/**
	 * constructor:
	 * create a new object with only the Mac Address known (minimum requirement)
	 * throw exception if the Mac Address was invalid
	 */
	public MAC_samples (String newMac) throws IllegalArgumentException
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
	}

	/**
	 * constructor:
	 * checks Mac address validity (throws IllegalArgumentException if invalid Mac address)
	 * sets the ESSID
	 */
	public MAC_samples (String essid, String newMac, int ch, int x, int y)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		X = x ;
		Y = y ;
		Essid = essid ;
		channel = ch ;
		isLocated = true ;
	}

	public MAC_samples (String essid, String newMac, int ch, Boolean Auth)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		Essid = essid ;
		channel = ch ;
		isLocated = false ;
		setAuthorized(Auth);
	}


	public MAC_samples (String essid, String newMac, int ch, sample samp)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		Essid = essid ;
		channel = ch ;
		isLocated = false ;
		this.addSample(samp);
//		sampleArr.add(samp);
	}

	public MAC_samples (String essid, String newMac, int ch, Boolean Auth, int x, int y)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		X = x ;
		Y = y ;
		Essid = essid ;
		channel = ch ;
		isLocated = true ;
		setAuthorized(Auth);
	}

	/**
	 * checks if the mac address is a valid one
	 */
	public static boolean isValidMac (String mac)
	{
	    Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){5}([0-9A-Fa-f]{2})$");
	    Matcher matcher = pattern.matcher(mac);
	    if (!matcher.matches())
	        return false;
	    return true ;
	}

	/*
	public ArrayList <sample> getSampleArr()
	{
		return sampleArr;
	}
	*/
	
	/*
	public void addSample(float sig ,int x, int y)
	{
		//PaintPane.mySamples.add(new sample(sig, x, y));
		sampleArr.add(new sample(sig, x, y));
	}
	*/
	public void addSample(sample s)
	{
		//PaintPane.mySamples.add(s);
		sampleArr.add(s);
	}
	
	public boolean isLocated()
	{
		return isLocated;
	}
	
	public boolean isRepresented()
	{
		return isRepresented;
	}

	public void setRepresented(boolean b)
	{
		isRepresented = b ;
	}

	
	/*
	 * I did these to keep the sampleArr private
	 */
	public int getS_X(int index)
	{
		return sampleArr.get(index).getX();
	}
	public int getS_Y(int index)
	{
		return sampleArr.get(index).getY();
	}
	public float getS_RSSI(int index)
	{
		return sampleArr.get(index).getSignal();
	}
	public int getSampleCount()
	{
		return sampleArr.size();
	}
	
	public String printSig_X_Y(int index)
	{
		String ret = Float.toString(sampleArr.get(index).getSignal())+"," + sampleArr.get(index).getX()+","+sampleArr.get(index).getY();
		return ret;
	}
	
	public String printAP_X_Y()
	{
		return X+","+Y;
	}
	
	public void setMacAddress(String mac_add)
	{
	    if (isValidMac(mac_add)) 
	    	MacAddress = mac_add;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + mac_add);
	}

	public void setChannel(int ch)
	{
		channel = ch;
	}

	/*
	 * returns the Mac address as String in the default form: 6 2Hexadicimal digits separated by ":"
	 */
	
	public void setLocation(int x, int y)
	{
		X = x;
		Y = y;
	}
	
	public String getMacAddress()
	{
		return MacAddress;
	}

	/*
	 * returns the Mac address as String in the default form: 6 2Hexadicimal digits separated by "-"
	 */
	public String getMac_Address()
	{
		return MacAddress.replaceAll(":", "-");
	}

	public void setESSID(String essid)
	{
		Essid = essid; 
	}
	
	public String getESSID()
	{
		return Essid;
	}
	
	public int getChannel()
	{
		return channel ;
	}

	public void setAuthorized(Boolean auth)
	{
		isAuth = auth;
	}
	
	public boolean isAuthorized()
	{
		return isAuth ;
	}
	
	public int getApX()
	{
		return X;
	}
	
	public int getApY()
	{
		return Y;
	}
}