package ohgarmin;

public class Karte implements Int_Karte{
	private double latitude;
	private double longitude;
	private int width;
	private int height;
	private int zoom;
	private String provider;
	private double mess_m;
	private double mess_px;
	private int zoomlevel;
	private int mapproviderid;
	
	public double getLatitude() {
		// gibt von der Karte die Latitude aus = Breitengrad
		return this.latitude;
	}
	
	public int getMapProviderId(){
		//gibt den ID-Wert vom Map - Provider aus 
		//0 = kein Provider
		//1 = Google
		//2 = Expedia
		if (this.provider.equals("Google")){
			return 1;
		}else if (this.provider.equals("Expedia")){
			return 2;
		}else return 0;
	}

	public double getLongitude() {
		// gibt von der Karte die Longitude aus = Längengrad
		return this.longitude;
	}

	public double getMess_M() {
		// gibt die gemessene Länge vom Maßstab aus
		// Die Werte werden vom Map-Provider zur Verfügung gestellt 
		return this.mess_m;
	}

	public double getMess_Px() {
		// gibt die Pixelwerte vom Maßstab aus
		// Die Werte wurden per Desktop-Ruler gemessen
		return this.mess_px;
	}

	public String getProvider() {
		// gibt den KartenProvider aus
		return this.provider;
	}

	public int getheight() {
		// gibt die Höhe der Karte aus
		return this.height;
	}

	public int getwidth() {
		// gibt die Breite der Karte aus
		return this.width;
	}
	
	public int getZoomlevel(){
		//gibt den Zoomlevel aus
		return this.zoomlevel;
	}
	
	public int getZoom(){
		//gibt den realen Zoomwert
		return this.zoom;
	}

	public void setHeight(int height) {
		// setzt die Höhe
		this.height = height;
	}

	public void setLatitude(double latitude) {
		// setzt den Breitengrad
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		// setzt den Längengrad
		this.longitude = longitude;
	}

	public void setMess_M(double Mess_M) {
		// setzt die gemessenen Werte
		this.mess_m = Mess_M;
	}

	public void setMess_Px(double Mess_Px) {
		// setzt die Desktop - Ruler Werte
		this.mess_px = Mess_Px;
	}

	public void setProvider(String Provider) {
		// setzt den MapProvider
		this.provider = Provider;
	}

	public void setWidth(int width) {
		// setzt die Breite
		this.width = width;
	}
	
	public void setZoomLevel(int zoomlevel){
		this.zoomlevel = zoomlevel;
	}
	
	
	public void setZoom (int zoomi, String provider){
		//Setzt den ZoomFaktor und alle Werte die man für den Maßstab braucht
		//verlangt wird zoomi der bei Google einen Wert von 0-9 hat oder bei Expedia 0-10
		// der Wert Provider darf den Wert "Google" oder "Expedia" haben
		this.setProvider(provider);
		if (provider.equals("Google")){
			switch(zoomi){
				case 0:{
					this.zoom = 1200;
					this.setMess_Px(65);
					this.setMess_M(50);
					this.zoomlevel = 0;
					break;	
				}
				case 1:{
					this.zoom = 2400;
					this.setMess_Px(65);
					this.setMess_M(100);
					this.zoomlevel=1;
					break;
				}
				case 2:{
					this.zoom = 4800;
					this.setMess_Px(65);
					this.setMess_M(200);
					this.zoomlevel=2;
					break;
				}
				case 3:{
					this.zoom = 9600;
					this.setMess_Px(80);
					this.setMess_M(500);
					this.zoomlevel=3;
					break;
				}
				case 4:{
					this.zoom = 19200;
					this.setMess_Px(80);
					this.setMess_M(1000);
					this.zoomlevel=4;
					break;
				}
				case 5:{
					this.zoom = 38400;
					this.setMess_Px(80);
					this.setMess_M(2000);
					this.zoomlevel=5;
					break;
				}
				case 6:{
					this.zoom = 76800;
					this.setMess_Px(100);
					this.setMess_M(5000);
					this.zoomlevel=6;
					break;
				}
				case 7:{
					this.zoom = 180000;
					this.setMess_Px(100);
					this.setMess_M(10000);
					this.zoomlevel=7;
					break;
				}
				case 8:{
					this.zoom = 600000;
					this.setMess_Px(100);
					this.setMess_M(20000);
					this.zoomlevel=8;
					break;
				}	
				case 9:	{
					this.zoom = 1800000;
					this.setMess_Px(125);
					this.setMess_M(50000);
					this.zoomlevel=9;
					break;
				}
			}
		}else if (provider.equals("Expedia")){
			switch(zoomi){
				case 0:{
					this.zoom = 1;
					this.zoomlevel = 0;
					this.setMess_M(200);
					this.setMess_Px(210);
					break;	
				}
				case 1:{
					this.zoom = 3;
					this.zoomlevel = 1;
					this.setMess_M(500);
					this.setMess_Px(175);
					break;		
				}
				case 2:{
					this.zoom = 6;
					this.zoomlevel = 2;
					this.setMess_M(1000);
					this.setMess_Px(175);
					break;	
				}
				case 3:{
					this.zoom = 12;
					this.zoomlevel = 3;
					this.setMess_M(2000);
					this.setMess_Px(175);
					break;
				}
				case 4:{
					this.zoom = 25;
					this.zoomlevel = 4;
					this.setMess_M(5000);
					this.setMess_Px(210);
					break;	
				}
				case 5:{
					this.zoom = 50;
					this.zoomlevel = 5;
					this.setMess_M(10000);
					this.setMess_Px(210);
					break;	
				}
				case 6:{
					this.zoom = 150;
					this.zoomlevel = 6;
					this.setMess_M(20000);
					this.setMess_Px(142);
					break;
				}
				case 7:{
					this.zoom = 800;
					this.zoomlevel = 7;
					this.setMess_M(100000);
					this.setMess_Px(132);
					break;	
				}
				case 8:{
					this.zoom = 2000;
					this.zoomlevel = 8;
					this.setMess_M(200000);
					this.setMess_Px(105);
					break;	
				}
				case 9:{
					this.zoom = 7000;
					this.zoomlevel = 9;
					this.setMess_M(1000000);
					this.setMess_Px(150);
					break;
				}
				case 10:{
					this.zoom = 12000;
					this.zoomlevel = 10;
					this.setMess_M(2000000);
					this.setMess_Px(175);
					break;
				}
			}
		}	
	}
	
	//Gibt den maximalen ZoomLevel für einen Provider aus
	public int getMaxZoomLevel() {
		if (this.provider.equals("Google")){
			return 9;
		}else if (this.provider.equals("Expedia")){
			return 10;
		}else{
			return 0;
		}
	}

}

