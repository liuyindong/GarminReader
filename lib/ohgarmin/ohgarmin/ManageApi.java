package ohgarmin;

import java.util.Vector;
import java.util.Date;
import javax.swing.*;

public class ManageApi
{
	int WPCounter=0;
	boolean Started=false;
	boolean debug=false;
	boolean WPrecieved;
	GarminApi app;
	Vector WPL = new Vector();
	Vector WPL2 = new Vector();
	Vector <TrackListe> LocalTL = new Vector<TrackListe>();
	WayPoint x = new WayPoint();
	Track myTrack=new Track();
	FotoDate fd = new FotoDate(1,1,1970); //Datum für fotos
	FotoDate gd = new FotoDate(1,1,1990); //Datum für Garmin-Tracks
	Date aktDat;
	

	public void sendWaypoints(double x, double y)
	{
		char[] ID = new char[6];
		String counterStr;
		
		if (!isStarted())
			startApi();
		
		ID[0]='O'; ID[1]='H'; ID[2]='W'; ID[3]='P';
		WPCounter++;	
		counterStr = Integer.toString(WPCounter);
		
		if (counterStr.length()==1) 
			counterStr = "0"+counterStr; 
		
		ID[4]=counterStr.charAt(0);
		ID[5]=counterStr.charAt(1);
		//System.out.println("id: "+ID.toString()+", x "+x+", y"+y);
		app.createWayPoint(ID, x, y);
	}
	
	private void AddLocalTracks()
	{
		Track a = new Track();	Track b = new Track();	Track c = new Track();
		Track d = new Track();	Track e = new Track();	
		
		a.setLongitude(16.28); a.setLatitude(48.380); a.setTime(582886790);
		b.setLongitude(16.30); b.setLatitude(48.402); b.setTime(582886799);
		c.setLongitude(16.25); c.setLatitude(48.366); c.setTime(582886903);
		d.setLongitude(16.29); d.setLatitude(48.409); d.setTime(582886925);
		e.setLongitude(16.27); e.setLatitude(48.391); e.setTime(582887003);
		
		a.setTemp(1337); b.setTemp(1234); c.setTemp(120985);
		
		((TrackListe)WPL.elementAt(0)).add(a);
		((TrackListe)WPL.elementAt(0)).add(b);
		((TrackListe)WPL.elementAt(0)).add(c);	
		((TrackListe)WPL.elementAt(0)).add(d);
		((TrackListe)WPL.elementAt(0)).add(d);
		System.out.println("long gleich nach anlegen:" +a.getLongitude());
	}
	
	public ManageApi()
	{ 
		WPrecieved=false;
	}
	
	public void startApi(){
		app=new GarminApi();
		Started=true;
	}
	//Koordinaten aus dem Gerät gelesen
	double distanz;
	public boolean isStarted(){
		return Started;
	}
	
	public boolean hasWpReceived()
	{
		return WPrecieved;
	}
	
	public void getWaypoints(){
		WPL = app.getTrackRoute();
		aktDat = new Date();
		System.out.println("Waypointanzahl: " + ((TrackListe)WPL.elementAt(0)).getSize() + " ; "+((TrackListe)WPL.elementAt(1)).getSize());
		AddLocalTracks();
		if (WPL.size() > 0) WPrecieved=true;		
	}
	
	
	public long getpreviosWPTime(long Time)
	{
		long erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;

		//debug=true;
		if (debug)
		for (int i=0;i<WPL.size();i++)
		{
			System.out.println("Route "+i+": "+ ((TrackListe)WPL.elementAt(i)).toString());
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
				System.out.println("Track "+j+": "+ ((TrackListe)WPL.elementAt(i)).getTrack(j).toString());
		}
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
		
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=myTrack.getTime()-FotoTime;
				if (help>0)
				{
					System.out.println("diftime:" + help);
					
					if (help<lasttimedif && help > 0)
					{
						//System.out.println("Prev Waypoint found");
						lasttimedif=help;
						TrackID=j; //dieser Wegpunkt is der beste biser
						RouteID=i; //route und track werden gesichert
						myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
						erg=myTrack.getTime();
					}
				}
				
			}
		}
		if (TrackID>0) 
		{
			//System.out.println("Trackid: "+ TrackID + " RouteID: " + RouteID + " erg: " + erg );
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getTime();
			System.out.println("erg: " + erg);
		}
		return erg;
	}
	
	public long getnextWPTime(long Time)
	{
		long erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
	
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=FotoTime-myTrack.getTime();
				
				if (help<lasttimedif && help > 0)
				{
					//System.out.println("next Waypoint found");
					lasttimedif=help;
					TrackID=j; //dieser Wegpunkt is der beste biser
					RouteID=i; //route und track werden gesichert
					myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
					erg=myTrack.getTime();
				}
			}
		}
		if (TrackID>0) 
		{
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getTime();
		}
		return erg;		
	}
	
	public double getpreviosWPX(long Time)	
	{
		double erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
	
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=myTrack.getTime()-FotoTime;

				if (help<lasttimedif && help > 0)
				{
					lasttimedif=help;
					TrackID=j; //dieser Wegpunkt is der beste biser
					RouteID=i; //route und track werden gesichert
					myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
					erg=myTrack.getLongitude();
				}
			}
		}
		if (TrackID>0) 
		{
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getLongitude();
		}
		return erg;
	}
	
	public double getpreviosWPY(long Time)
	{
		double erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
	
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=myTrack.getTime()-FotoTime;

				if (help<lasttimedif && help > 0)
				{
					lasttimedif=help;
					TrackID=j; //dieser Wegpunkt is der beste bisher
					RouteID=i; //route und track werden gesichert
					myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
					erg=myTrack.getLatitude();
				}
			}
		}
		if (TrackID>0) 
		{
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getLatitude();
		}
		return erg;	
	}
	
	public double getnextWPX(long Time)
	{
		double erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
	
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=FotoTime-myTrack.getTime();

				if (help<lasttimedif && help > 0)
				{
					lasttimedif=help;
					TrackID=j; //dieser Wegpunkt is der beste biser
					RouteID=i; //route und track werden gesichert
					myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
					erg=myTrack.getLongitude();
				}
			}
		}
		if (TrackID>0) 
		{
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getLongitude();
		}
		return erg;		
	}
	
	public double getnextWPY(long Time)
	{
		double erg=-1;
		long lasttimedif=Time, help=0;
		int TrackID=-1;
		int RouteID=-1;
		long FotoTime=Time;
		
		FotoTime=Time;
		long time2 = (fd.getMS(1,1,1970,1,1,1990))/1000;
		FotoTime=FotoTime-time2; //zieht von Fototime die jahre 1970-1990 ab -> sollte dan gleich sein mit Garmintime
	
		for (int i=0;i<WPL.size();i++)
		{
			for (int j=0;j<((TrackListe)WPL.elementAt(i)).getSize();j++)
			{
				myTrack=((TrackListe)WPL.elementAt(i)).getTrack(j);
				help=FotoTime-myTrack.getTime();

				if (help<lasttimedif && help > 0)
				{
					lasttimedif=help;
					TrackID=j; //dieser Wegpunkt is der beste bisher
					RouteID=i; //route und track werden gesichert
					myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
					erg=myTrack.getLatitude();
				}
			}
		}
		if (TrackID>0) 
		{
			myTrack=((TrackListe)WPL.elementAt(RouteID)).getTrack(TrackID);
			erg=myTrack.getLatitude();
		}
		return erg;			
	}
}
