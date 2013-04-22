package ohgarmin;

public interface Int_Karte {
		//Setter
	public void setLatitude(double latitude);
	public void setLongitude(double longitude);
	public void setWidth(int width);
	public void setHeight(int height);
	public void setMess_Px(double Mess_Px);
	public void setMess_M(double Mess_M);
	public void setProvider(String Provider);
	public void setZoom (int zoomi, String provider);
	public void setZoomLevel(int zoomlevel);
		
		//Getter
	public double getLatitude();
	public double getLongitude();
	public int getwidth();
	public int getheight();
	public double getMess_Px();
	public double getMess_M();
	public String getProvider();
	public int getMapProviderId();
	public int getZoomlevel();
	public int getZoom();
	public int getMaxZoomLevel();
}
