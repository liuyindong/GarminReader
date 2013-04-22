package ohgarmin;
import java.util.*;
import java.io.*;
import java.awt.*;
public class WegPunkte implements Int_WegPunkte{
	private Vector VectorX = new Vector(); //speichert X-Koordinaten der Wegpunkte 
	private Vector VectorY = new Vector(); //speichert Y-Koordinate der Wegpunkte
	private Karte map; //speichert map (Informationen zu Gr��e und Koordinaten)
	private Calculate calc; //Calc-Klasse f�r Umrechnungen
	private Vector VectorImage = new Vector(); //speichert die Fotos des Users
	private int markedFoto=0; //speichert, welches Foto gerade markiert wird
	
	public void setMap(Karte map2)
	{ //die �bergebene map wird zugewiesen
		map = map2; 
	}
	
	public void setCalculate(Calculate calc2)
	{ //das �bergebene Calk-Objekt wird zugewiesen
		calc = calc2;
	}
	
	public void checkResize(int x, int y)
	{//�berpr�ft ob die Maus auf einem foto ist, wenn ja wird diese als "marked" gekennzeichnet	
		FotoObject FO;
		for (int i= 0; i < VectorImage.size(); i++)
		{ //Vector wird durchlaufen
			FO = (FotoObject)VectorImage.elementAt(i); //FO ist hilfszeiger auf das Object im Vector					
			
			if (x > FO.getX()-(FO.getFotoSmallSize()/2) && x < FO.getX()+ (FO.getFotoSmallSize()/2) && 
				y > FO.getY()-(FO.getFotoSmallSize()/2) && y < FO.getY()+ (FO.getFotoSmallSize()/2))
			{//wenn x und y auf dem foto liegen ->
				      FO.setMarked(); //foto wird markiert	
				      markedFoto = i;
			}
			else //ansonsten wird es demarkiert
			{
				FO.setUnMarked();
				markedFoto = 0;
			}
		}
	}
	
	//Methode erwartet 2 Double Werte - Longitude & Latitude
	// und f�gt diese als StringObjekt in die 2 Vektoren
	public void addWayPoint(double lati, double longi) {
		VectorY.add(""+lati); 
		VectorX.add(""+longi);
	}
	
	public void addWayPointImage(FotoObject x){				
		VectorImage.add(x); //In den Vector kommt ein neues Foto		
	}
		
	//Erwartet die Stelle vom Vektor die gel�scht werden soll
	//entfernt diese Elemente aus den Vektoren
	public void removeWayPoint(int Index) 
	{
		VectorY.removeElementAt(Index); //l�scht Aus den koordinatenVektoren die Wegpunkte
		VectorX.removeElementAt(Index); 
	}

	public void saveWayPoints(ManageApi manager)
	{
		double x,y;
		
		for (int i= 0; i < VectorY.size(); i++){ //Vector wird druchlaufen
			x = Double.parseDouble((VectorX.elementAt(i).toString()));
			y=  Double.parseDouble((VectorY.elementAt(i).toString())); 
			manager.sendWaypoints(x, y);
		}
		
		// TODO Auto-generated method stub		
	}
	public void saveWayPoints()
	{
			
	}
	

	//Bekommt eine Grafik zugewiesen und zeichnet alle WegPunkte auf dieser Grafik drauf
	public void showWayPoints(Graphics g) 
	{
		double longix; //GPS-Länge
		double laty;	//GPS-Höhe
		int pixelx=0; //Pixel-X-Koordinaten
		int pixely=0; //Pixel-Y-Koordinaten
		Graphics2D g2 = (Graphics2D)g; //Graphicsobject zum zeichnen
		g2.setStroke(new BasicStroke( 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER )); //Strichst�rke
		g2.setFont(new Font( "Verdana", Font.BOLD, 20 )); //Schrift wird eingestellt
		for (int x= 0; x < VectorY.size(); x++){ //Vector wird druchlaufen			
			longix = Double.parseDouble((VectorX.elementAt(x).toString())); //GPS-L�nge wird herausgelesen
			laty = Double.parseDouble((VectorY.elementAt(x).toString())); //GPS-H�he wird herausgelesen
			g2.setColor(Color.BLUE);
			pixelx = (int) calc.GradToPixelX(map.getLongitude()-longix); //mittels GPS-Koordinaten und Map-Mittelpunkt wird ermittelt
			pixely = (int) calc.GradToPixelY(map.getLatitude()-laty);	//an welchen Pixel-Koordinaten der Wegpunkt gezeichnet werden muss
			g2.drawOval(map.getwidth()/2-pixelx-5,map.getheight()/2+pixely-5,10, 10); //ein Kreis wird an diesem punkt gezeichnet
			g2.setColor(Color.BLACK); 
			g2.drawString(""+(x+1), map.getwidth()/2-pixelx+10, map.getheight()/2+pixely+10); //Wegpunktzahl wird gezeichnet
			//System.out.println("!!!! PixelX: "+pixelx+" PixelY: "+pixely+" longiX: "+longix+" laty: "+laty);
		}		
	}
	
	public void showImages(Graphics g,ImagePanel imagepanel)
	{//Zeichnet alle Fotos des Users
		double longix; //GPS-Länge
		double laty;  //GPS-Höhe
		int pixelx=0; //Pixel-X-Koordinaten
		int pixely=0; //Pixel-Y-Koordinaten
		int size=0;   //Breite/Höhe des Fotos
		FotoObject FO; //Fotoobject
		Graphics2D g2 = (Graphics2D)g; //Graphics-Objekt zum Zeichnen
		g2.setStroke(new BasicStroke( 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ));
		g2.setFont(new Font( "Verdana", Font.BOLD, 20 ));
		g2.setColor(Color.BLUE);
		
		for (int x= 0; x < VectorImage.size(); x++)
			
		{   FO = (FotoObject)VectorImage.elementAt(x);//FO zeigt auf das FotoObject im Vector
			longix = FO.getGPSX(); //GPS-Koordinaten werden ermittelt
			laty = FO.getGPSY();   		
			pixelx = (int) calc.GradToPixelX(map.getLongitude()-longix); //analog zu den Wegpunkten werden die GPS-Koordinaten
			pixely = (int) calc.GradToPixelY(map.getLatitude()-laty); //in Pixel-Koordinaten (relativ zur mitte) umgerechnet
			
			FO.setX(map.getwidth()/2-pixelx); //In das Fotoobject werden diese Pixelkoordinaten gespeichert (aber absolute werte) 
			FO.setY(map.getheight()/2+pixely); //(das ist nötig für die CheckResize-Funktion
			
			Image image = (Image) FO.getImage(); //Das Foto selbst wird herausgeladen
			
			if (FO.isMarked()) //Das Bild ist markiert - es wird größer gezeichnet			
				size=FO.getFotoBigSize(); //size wird groß gestellt 			
			else
				size=FO.getFotoSmallSize(); //size wird klein gestellt
			
			g2.drawImage(image,map.getwidth()/2-pixelx-(size/2), map.getheight()/2+pixely-(size/2),size,size,imagepanel);
			//Bild wird gezeichnet, um die Hälfte der Bildgröße nach linksoben versetzt (damit mittelpunk richtig is)	
			//System.out.println("PixelX: "+pixelx+" PixelY: "+pixely+" longiX: "+longix+" laty: "+laty);
		}	
		
	   /* if (markedFoto > 0) //Das markierte Foto wird nochmals gezeichnet, damit es von keinem anderen �berlappt wird
		{
			FO = (FotoObject)VectorImage.elementAt(markedFoto);//FO zeigt auf das FotoObject im Vector
			longix = FO.getGPSX(); laty = FO.getGPSY();
			pixelx = (int) calc.GradToPixelX(map.getLongitude()-longix); //analog zu den Wegpunkten werden die GPS-Koordinaten
			pixely = (int) calc.GradToPixelY(map.getLatitude()-laty); //in Pixel-Koordinaten (relativ zur mitte) umgerechnet			
			FO.setX(map.getwidth()/2-pixelx); //In das Fotoobject werden diese Pixelkoordinaten gespeichert (aber absolute werte) 
			FO.setY(map.getheight()/2+pixely); //(das ist nötig für die CheckResize-Funktion			
			Image image = (Image) FO.getImage(); //Das Foto selbst wird herausgeladen					
			size=FO.getFotoBigSize(); //size wird groß gestellt 									
			g2.drawImage(image,map.getwidth()/2-pixelx-(size/2), map.getheight()/2+pixely-(size/2),size,size,imagepanel);	
		} */
		
	}
	
	//Listet alle WegPunkte auf
	public void listWayPoints(){
		System.out.println("******************");
		System.out.println("WegPunkte");
		System.out.println("******************");
		for (int x= 0; x < VectorY.size(); x++){
			System.out.println(x+". "+VectorY.elementAt(x));
			System.out.println(VectorX.elementAt(x));
		}
	}

}
