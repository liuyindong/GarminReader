package ohgarmin;

import java.text.DecimalFormat;

 public class Calculate implements Int_Berechnung {
	public final int ERDRADIUS = 6370; //Radius der Erde in Metern 
	public  final double LAT_KILOMETER = 111.32; //Abstand zweier Breitengrade in KM
	public Karte map;
	
	public Calculate(Karte map2){
		//Standardwerte
		map = map2;
	}
	

	public double PixelToMeter(int PixelDistanz){ //bekommt Distanz in Pixel, rechnet um auf Meter
		return PixelDistanz*(map.getMess_M()/map.getMess_Px());
	}
	
	public double PixelToGradX(int PixelDistanz){//bekommt Distanz in Pixel, rechnet um auf Grad
		double ergebnis = (Math.cos(Math.toRadians(map.getLatitude())))*2*Math.PI*ERDRADIUS/360;
		double ergebnis2 = 1 / ergebnis * 1000;
		return (map.getLongitude()*1000000 + ergebnis2 * this.PixelToMeter((PixelDistanz)))/1000000;
	}
	
	public double PixelToGradY(int PixelDistanz){//bekommt Distanz in Pixel, rechnet um auf Grad
		double ergebnis2 = 1 / LAT_KILOMETER *1000;
		return (map.getLatitude()*1000000 - ergebnis2 * this.PixelToMeter((PixelDistanz)))/1000000;
	}
	
	public int MeterToPixel(double MeterDistanz){//bekommt Distanz in Meter, rechnet um auf Pixel
		return (int) (MeterDistanz * (map.getMess_Px() / map.getMess_M()));
	}
	
	public double MeterToGradX(double MeterDistanz){//bekommt Distanz in Meter, rechnet um auf Grad
		//F�R L�NGENGRADE
		double ergebnis = (Math.cos(Math.toRadians(map.getLatitude())))*2*Math.PI*ERDRADIUS/360;
		double ergebnis2 = 1 / ergebnis * 1000;
		return (map.getLongitude()*1000000 + (ergebnis2 * MeterDistanz))/1000000;
	}
	
	public double MeterToGradX_Foto(double MeterDistanz, double FotoLatitude){//bekommt Distanz in Meter, rechnet um auf Grad
		//F�R L�NGENGRADE
		double ergebnis = (Math.cos(Math.toRadians(FotoLatitude)))*2*Math.PI*ERDRADIUS/360;
		double ergebnis2 = 1 / ergebnis * 1000;
		return (ergebnis2 * MeterDistanz);
	}
	
	public double MeterToGradY(double MeterDistanz){//bekommt Distanz in Meter, rechnet um auf Grad
		//F�R BREITENGRADE
		double ergebnis = 1 / LAT_KILOMETER *1000;
		return (map.getLatitude()*1000000 - (ergebnis * (MeterDistanz-map.getheight()/2)))/1000000;
	}

	public double GradToMeterX(double GradDistanz){ //bekommt Distanz in Grad, rechnet um auf Meter		
		//Formel zum Berechnen des Abstandes zweier L�ngengrade in Meter
		double ergebnis = (Math.cos(Math.toRadians(map.getLatitude())))*2*Math.PI*ERDRADIUS/360;		
		return ergebnis*1000*GradDistanz;	
	}
	
	public double GradToMeterY(double GradDistanz){ //bekommt Distanz in Grad, rechnet um auf Meter				
		//F�R BREITENGRADE
		return (LAT_KILOMETER)* 1000 * GradDistanz;	
	}
	
	public int GradToPixelX(double GradDistanz){ //bekommt Distanz in Grad, rechnet um auf Pixel
		//F�R L�NGENGRADE
		return MeterToPixel(GradToMeterX(GradDistanz));
	}
	
	public int GradToPixelY(double GradDistanz){ //bekommt Distanz in Grad, rechnet um auf Pixel
		//F�R BREITENGRADE
		return MeterToPixel(GradToMeterY(GradDistanz));
	}
	
	public int DistanzToMidX_Pixel(int PixelX){ //Berecnet horizontalen Abstand zur Mitte 
	 // Bekommt X-Koordinate in Pixel, retourniert Betrag vom Abstand in Pixel
		return ((-1)*((map.getwidth()/2)-PixelX));
	}
	
	public int DistanzToMidY_Pixel(int PixelY){ //Berechnet vertikalen Abstand zur Mitte 
		 // Bekommt Y-Koordinate in Pixel, retourniert Betrag vom Abstand in Pixel
		return (((map.getheight()/2)-PixelY)*(-1));
	}
	
	public double DistanzToMidX_Meter(double MeterX){ //Berechnet horizontalen Abstand zur Mitte 
		 // Bekommt X-Koordinate in Meter, retourniert Betrag vom Abstand in Meter
		return (MeterX-PixelToMeter(map.getwidth()/2));
	}
	
	public double DistanzToMidY_Meter(double MeterY){ //Berechet vertikalen Abstand zur Mitte 
		 // Bekommt Y-Koordinate in Pixel, retourniert Betrag vom Abstand	in Meter
		return (MeterY-PixelToMeter(map.getheight()/2));
	}
	
	public double DistanzToMidX_Grad(double GradX){ //Berechnet horizontalen Abstand zur Mitte 
		 // Bekommt X-Koordinate in Pixel, retourniert Betrag vom Abstand in Grad
		return (GradX-PixelToGradX(map.getwidth()/2));
	}
	
	public double DistanzToMidY_Grad(double GradY){ //Berechnet vertikalen Abstand zur Mitte 
		 // Bekommt Y-Koordinate in Pixel, retourniert Betrag vom Abstand in Grad
		return (GradY-PixelToGradY(map.getheight()/2));
	}
	
	public String WertFormatieren(double wert)
	{//Liefert den wert als String zur�ck, der STring enth�l eine Zahl mit 2 Vorkomma und 5 Nachkommastellen
		DecimalFormat format = new DecimalFormat("##.#####");
		return ""+format.format(new Double(wert)); 
	}

	public double getNextEast(int width) {
		// TODO Auto-generated method stub
		return this.PixelToGradX(150);
	}

	public double getNextNorth(int height) {
		// TODO Auto-generated method stub
		return this.PixelToGradY(-150);
	}

	public double getNextSouth(int height) {
		// TODO Auto-generated method stub
		return this.PixelToGradY(150);
	}

	public double getNextWest(int width) {
		// TODO Auto-generated method stub
		return this.PixelToGradX(-150);
		
	}
	
}
