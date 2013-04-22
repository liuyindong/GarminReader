package ohgarmin;

import java.io.*;
import java.net.*;
import java.text.*;

public class Wget implements Int_Wget{
	private String dateiname;
	private String output;
	private int mapprovider;
	private String mapdir;
	Karte map;
	
	public Wget(Karte map2){
		map = map2;
		this.mapdir="maps";
	}
	
	public void setOutput(String output){
		this.output = mapdir+"/"+ output;
	}
	
	//Wandelt einen Double Wert in einen String - 
	//das Format wird speziell für den jeweiligen Provider angepasst
	
	public String setGoogleString(double Wert){
		Wert = Wert*1000000;
		DecimalFormat format = new DecimalFormat("#########");
		return ""+format.format(new Double(Wert));
	}
	public String setExpediaString(double Wert){
		Wert = Wert * 1.000000;
		DecimalFormat format = new DecimalFormat("##.00000");
		return ""+format.format(new Double(Wert));
	}
	
	//Die ganze Url für den jeweiligen Provider
	public String getWholeUrlGoogle(){
		return "http://maps.google.com/mapdata?latitude_e6="+this.setGoogleString(map.getLatitude())+"&longitude_e6="+this.setGoogleString(map.getLongitude())+"&zm="+map.getZoom()+"&w="+map.getwidth()+"&h="+map.getheight()+"&cc=ES&min_priority=5";
	}
	public String getWholeUrlExpedia(){
		return "http://www.expedia.de/pub/agent.dll?qscr=mrdt&ID=3kQaz.&CenP="+this.setExpediaString(map.getLatitude())+","+this.setExpediaString(map.getLongitude())+"&Lang=EUR0407&Alti="+map.getZoom()+"&Size="+map.getwidth()+","+map.getheight()+"&Offs=0.000000,0.000000&Pins=|8947|";
	}
	
	public String getOutput(){
		return this.output;
	}
	
	//Getter für die Dateiendung JPG
	public String getPostOutput(){
		return this.output+".jpg";
	}
	
	//Kontrolliert ob schon ein MapOrdner existiert, wenn nicht soll einer erstellt werden
	public void dirCheck(String dirname){
		if (fileCheck(dirname) != true){
			new File(dirname).mkdir();
		}
	}
	
	//Kontrolliert ob eine gewisse Datei schon besteht
	public boolean fileCheck(String dateiname){
		if (new File (dateiname).exists()){
			return true;
		}else{
			return false;
		}
	}
	
	//Methode zum Herunterladen eines Bildes
	//die dann unter ouptut gespeichert werden
	public void download(){
      //Benutz die Url
	  this.dirCheck(this.mapdir);
	  if (!fileCheck(this.getPostOutput())){
		  switch (map.getMapProviderId()){
	  		case 1: {
	  				try{
	  					URL url = new URL(this.getWholeUrlGoogle());
	  					System.out.println("Karte wird von Google Maps downgeloadet: ");
	  					System.out.println(""+url);
	  					//Öffnet eine Connection
	  					HttpURLConnection con = (HttpURLConnection)url.openConnection(); 
	  					//Erzeugen eines OutputStreams	
	  					FileOutputStream out = new FileOutputStream(new File(this.getPostOutput()));
	  					//Erzeugen eines InputStreams
	  					InputStream in = con.getInputStream();
	  					int n;
	  					byte[] buf = new byte[4096];
	  					//Liest Outputstreams und schreibt in den InputStreams
	  					while( (n = in.read(buf)) > 0) 
	  					{
	  						out.write(buf, 0, n);
	  					}
	  					//Beenden der Streams
	  					in.close();
	  					out.close(); 
	  				}catch(IOException ex){
	  			
	  				}
	  				break;
		  		}
		  		case 2: {
		  			try{
		  				URL url = new URL(this.getWholeUrlExpedia());
		  				System.out.println("Karte wird von Expedia downgeloadet: ");
		  				System.out.println(""+url);
		  				//Öffnet eine Connection
		  				HttpURLConnection con = (HttpURLConnection)url.openConnection(); 
		  				//Erzeugen eines OutputStreams	
		  				FileOutputStream out = new FileOutputStream(new File(this.getPostOutput()));
		  				//Erzeugen eines InputStreams
		  				InputStream in = con.getInputStream();
		  				int n;
		  				byte[] buf = new byte[4096];
		  				//Liest Outputstreams und schreibt in den InputStreams
		  				while( (n = in.read(buf)) > 0) 
		  				{
		  					out.write(buf, 0, n);
		  				}
		  				//Beenden der Streams
		  				in.close();
		  				out.close(); 
		  			}catch(IOException ex){
	  			
		  			}
		  			break;
		  		}
		  	}
	  	}
	}
}
