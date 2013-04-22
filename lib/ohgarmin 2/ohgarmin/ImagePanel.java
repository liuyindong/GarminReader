package ohgarmin;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

//Klasse ImagePanel
	
	public class ImagePanel extends JPanel
	{	
		BufferedImage image; //speichert den aktuellen Kartenausschnitt		
						
		WegPunkte waypoints= new WegPunkte(); //Speichert einen Vektor, der die Wegpunkte enth�lt			
		WegPunkte waypointsImage= new WegPunkte();	//Speichert einen Vektor, der alle geladenen Fotos enth�lt		
		
		//Bild zeichnen							
		public void paint( Graphics g ){ 
			if ( image != null ) {
				g.drawImage( image, 0, 0, this ); //Bild wird gezeichnet
							
			    setSize(image.getWidth(this), image.getHeight(this)); //Größe wird angepasst an das Fenster
			    waypointsImage.showImages(g,this); //Die Foto-paint-Funktion wird aufgerufen
			    waypoints.showWayPoints(g);  //Die Wegpunkte-paint-Funktion wird aufgerufen
			}
		}
		
		public void checkResize(int x, int y)
		{ //überprüft, ob auf Koordinaten x und y ein Foto liegt, und ändert dessen fotoSize
			waypointsImage.checkResize(x,y);
			repaint(); //alle Bilder werden neu gezeichnet
		}
		
		//Bild laden von einer Datei
		public void setImage( File file ) 
		  { 
		    try 
		    { 
		      if ( (image = ImageIO.read( file )) != null ) 
		      { //Datei konnte gelesen werden -> neues Image 
		        setPreferredSize( new Dimension(image.getWidth(), image.getHeight()) ); 		        
		        repaint();  //Größe wird angepasst und dann neu gezeichnet
		      } 
		    } 
		    catch ( IOException e ) 
		    { 
		    } 
		  }
		
		public void setWegPunkte(WegPunkte waypoints2)
		{//vom HautpGui bekommt man wegpunkte übergeben, diese sind die neuen aktuellen Wegpunkte 
			waypoints = waypoints2;
		}
		
		public void setWegPunkteImage(WegPunkte waypointsImage)
		{//Analog zu setWegPunkte, nur für Fotos
			this.waypointsImage = waypointsImage;
		}					
	}
	