package gui;

import java.util.ArrayList;

import soft.sample;

public class XY_sample {
/**
 * Parse samples by x & y 
 */
	
	private int X,Y;
	private float MaxRSSI ;
	private ArrayList<sample> sampleList;
	
	public XY_sample(int x, int y, sample s)
	{
		X = x;
		Y = y;
		sampleList = new ArrayList<sample>();
		sampleList.add(s);
		MaxRSSI = s.getSignal();
	}
	
	public XY_sample(int x, int y)
	{
		X = x;
		Y = y;
		sampleList = new ArrayList<sample>();
		MaxRSSI = -1000000000000000f;
	}
	
	public void addSample (sample s)
	{
		sampleList.add(s);
		if (s.getSignal() > MaxRSSI && s.toPaint())
			MaxRSSI = s.getSignal();
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public float getRSSI()
	{
		return MaxRSSI;
	}
	
	public void printXYRSSI()
	{
		System.out.println("**********************************");
		System.out.println(X + "," + Y + " RSSI: " + MaxRSSI);
		System.out.println("SAMPLELIST SIZE: " + sampleList.size());
		for (int i = 0; i < sampleList.size(); i++)
		{
			System.out.println(sampleList.get(i).toString());
		}
	}
	
	public void changeMaxRSSI()
	{
		MaxRSSI = -1000000;
		for (int i = 0; i < sampleList.size(); i++)
		{
			try
			{
				if (sampleList.get(i).toPaint())
					MaxRSSI = Math.max(MaxRSSI, sampleList.get(i).getSignal());
			}
			catch (NullPointerException e)
			{
				System.out.println("WAAAAA XY_sample.changeMaxRSSI()");
				// remove sample from list
			}
		}
	}
}
