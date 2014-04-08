package soft;
import soft.sample;
import java.util.ArrayList;

public class MAC_samples
{
	double RSSI;
	double X , Y;
	public String MacAddress;
	public String Essid;
	public ArrayList <sample> A = new ArrayList <sample> (); //save samples for each Mac address

	public MAC_samples()
	{
		MacAddress ="";
	}
	public MAC_samples (String MacAdd)
	{
		MacAddress = MacAdd;
	}

	public String getMac()
	{
		return MacAddress;
	}

	public String getESSID()
	{
		return Essid;
	}
}