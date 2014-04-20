package soft;
import soft.sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class MAC_samples
{
	double RSSI;
	double X , Y;
	private String MacAddress;
	private String Essid;
	private ArrayList <sample> sampleArr = new ArrayList <sample> (); //save samples for each Mac address
/*
	public MAC_samples()
	{
		MacAddress ="";
	}
*/
	public MAC_samples (String newMac) throws IllegalArgumentException
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
	}

	public MAC_samples (String essid, String newMac) throws IllegalArgumentException
	{
		Essid = essid ;
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
	}

	public MAC_samples (String newMac, int x, int y)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		X = x ;
		Y = y ;
	}

	public MAC_samples (String essid, String newMac, int x, int y)
	{
	    if (isValidMac(newMac)) 
	    	MacAddress = newMac;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + newMac);
		X = x ;
		Y = y ;
		Essid = essid ;
	}

	// checks if the mac address is a valid one
	public static boolean isValidMac (String mac)
	{
	    Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){5}([0-9A-Fa-f]{2})$");
	    Matcher matcher = pattern.matcher(mac);
	    if (!matcher.matches())
	        return false;
	    return true ;
	}

	public ArrayList <sample> getSampleArr()
	{
		return sampleArr;
	}
	
	public void addSample(float sig ,int x, int y)
	{
		sampleArr.add(new sample(sig, x, y));
	}

	public void addSample(sample s)
	{
		sampleArr.add(s);
	}
	
	public void setMacAddress(String mac_add)
	{
	    if (isValidMac(mac_add)) 
	    	MacAddress = mac_add;
	    else
	        throw new IllegalArgumentException("Invalid MAC address format: " + mac_add);
	}

	public String getMacAddress()
	{
		return MacAddress;
	}

	public void setESSID(String essid)
	{
		Essid = essid; 
	}
	
	public String getESSID()
	{
		return Essid;
	}
}