package ohgarmin;

import java.awt.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class HauptGui implements ActionListener,MouseListener,MouseMotionListener {
	private Karte map;
	private ImagePanel imagepanel;
	private Calculate calc;
	private Statusbar statusbar;
	private Wget wget;
	private JFrame frame;
	private WegPunkte waypoints;
	private boolean waypointflag = false; //Hilfsflag damit man rausfindet ob ma im WayPoint-Modus ist
	OnlyWindow onlywindow = new OnlyWindow(); // Hilfsobjekt damit nur eine Instanz von GuiDown rennt
	private String FotoPath = "//media//disk//DCIM//101MSDCF";
	        //       FotoPath = "//";
	private boolean pathset = false;
	private int FotoCounter = 0;
	private ManageApi ApiManager;
	private int count_wp_user=0;
	private int count_wp_fotos=0;
	final double Garmin_GPS_format = 11930464.74;
	
	public void guiCreate(){
		//Methode um das Grafische User Interface zu erstellen
	
		//Erstellung des HauptFrames
		frame = new JFrame("GpsFrank - The Better Choice ...");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(760,530);
		frame.setSize(780,580);
		//frame.setSize(780,580); -> für netbooks (10 zoll)
		frame.setResizable(false);
		frame.setVisible(true);
		//Menüleiste
		JMenuBar mb = new JMenuBar();
		
		//Hauptmenüpunkte
		JMenu file = new JMenu("Datei");
		JMenu edit = new JMenu("Bearbeiten");
		JMenu datalink = new JMenu ("Datenübertragung");
		JMenu help = new JMenu ("Hilfe");
		
		//Untermen�punkte
		//Unterpunkte von Datei
		JMenuItem saveas = new JMenuItem("Speichern unter");
		JMenuItem openmap = new JMenuItem("Karte öffnen");
		JMenuItem downmap = new JMenuItem("Karte downloaden");
		JMenuItem exit = new JMenuItem("Beenden");
		
		//Unterpunkte von Bearbeiten
		JMenuItem waypoint = new JMenuItem("WegPunktModus");
		JMenuItem zoom_in = new JMenuItem("Zoom +");
		JMenuItem zoom_out = new JMenuItem("Zoom -");
		
		//Unterpunkte von Daten�bertragung
		JMenuItem garmin_save = new JMenuItem("Wegpunkte auf Gerät schreiben");
		JMenuItem chose_pic_folder = new JMenuItem("Ordner für Fotos auswählen");
		//JMenuItem garmin_load = new JMenuItem("Daten vom Garmin laden");
		JMenuItem show_picture = new JMenuItem("Fotos anzeigen");
		
		//Unterpunkte f�r Hilfe
		JMenuItem helpitem = new JMenuItem("Hilfe");
		JMenuItem info = new JMenuItem("Info");
		
		frame.setJMenuBar(mb);
		mb.add(file);
		mb.add(edit);
		mb.add(datalink);
		mb.add(help);
		
		//file.add(saveas);
		//file.add(openmap);
		file.add(downmap);
		file.add(exit);
		
		edit.add(zoom_in);
		edit.add(zoom_out);
		edit.add(waypoint);
		
		datalink.add(garmin_save);
		//datalink.add(garmin_load);
		datalink.add(chose_pic_folder);
		datalink.add(show_picture);
		
		//help.add(helpitem);
		help.add(info);
		
		//Erzeugung der Richtungsbuttons
		JButton btn_north = new JButton("Norden");
		JButton btn_south = new JButton("Sueden");
		JButton btn_west = new JButton("Westen");
		JButton btn_east = new JButton("Osten");
		JPanel panel_statusbar = new JPanel();
		
		//Erzeugung von Statusbar
		statusbar = new Statusbar();
		
		//Erzeugung von ImagePanel
		imagepanel = new ImagePanel();
		imagepanel.setBackground(Color.blue);
		imagepanel.setSize(600,400);
		
		//Erzeugung von einer Karte
		map = new Karte();
		
		//Erzeugung von einem Calculate Objekt
		calc = new Calculate(map);
		
		//Erzeugung von einem Wget Objekt
		wget = new Wget(map);
		
		//Erzeugung von einem WegPunkt Objekt
		waypoints = new WegPunkte();
		
		//Erzeugung von einem ManageApi-Objekt
		ApiManager = new ManageApi();
				 
		//Einfügen der Komponenten auf dem Frame
		frame.getContentPane().add(BorderLayout.EAST,btn_east);
		frame.getContentPane().add(BorderLayout.WEST,btn_west);
		frame.getContentPane().add(BorderLayout.NORTH,btn_north);
		frame.getContentPane().add(BorderLayout.SOUTH,panel_statusbar);
		panel_statusbar.setLayout(new BorderLayout());
		panel_statusbar.add(btn_south,BorderLayout.NORTH);
		panel_statusbar.add(statusbar,BorderLayout.SOUTH);
		frame.getContentPane().add(BorderLayout.CENTER,imagepanel);
		
		//Hotkeys
		//Alt + Pfeiltasten
		btn_north.setMnemonic(java.awt.event.KeyEvent.VK_UP);
		btn_south.setMnemonic(java.awt.event.KeyEvent.VK_DOWN);
		btn_west.setMnemonic(java.awt.event.KeyEvent.VK_LEFT);
		btn_east.setMnemonic(java.awt.event.KeyEvent.VK_RIGHT);
		//Strg +-
		zoom_in.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS,java.awt.Event.CTRL_MASK));
		zoom_out.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS,java.awt.Event.CTRL_MASK));
		//Strg+D
		downmap.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,java.awt.Event.CTRL_MASK));
		//Strg+B
		exit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,java.awt.Event.CTRL_MASK));
		//Strg+W
		waypoint.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,java.awt.Event.CTRL_MASK));
		
		show_picture.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				loadFotos();									
			}
		});
		
		//EventHandler der einzelnen Buttons
		//Norden
		btn_north.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (map.getMess_M()!= 0){
					map.setLatitude(calc.getNextNorth(map.getheight()));
					wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
					wget.download();
					imagepanel.setImage(new File(wget.getPostOutput()));
					imagepanel.repaint();
				}
			}
		});
		//S�den
		btn_south.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (map.getMess_M()!= 0){
					map.setLatitude(calc.getNextSouth(map.getheight()));
					wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
					wget.download();
					imagepanel.setImage(new File(wget.getPostOutput()));
					imagepanel.repaint();
				}
			}
		});
		//Osten
		btn_east.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (map.getMess_M()!= 0){
					map.setLongitude(calc.getNextEast(map.getwidth()));
					wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
					wget.download();
					imagepanel.setImage(new File(wget.getPostOutput()));
					imagepanel.repaint();
				}
			}
		});
		//Westen
		btn_west.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (map.getMess_M()!= 0){
					map.setLongitude(calc.getNextWest(map.getwidth()));
					wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
					wget.download();
					imagepanel.setImage(new File(wget.getPostOutput()));
					imagepanel.repaint();
				}
			}
		});
		
		//Zoom In 
		zoom_in.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if (map.getMess_M() != 0){
					if (map.getZoomlevel() > 0){
						if(map.getProvider().equals("Google")){
							map.setZoom(map.getZoomlevel()-1, "Google");
						}else if (map.getProvider().equals("Expedia")){
							map.setZoom(map.getZoomlevel()-1, "Expedia");
						}
					}
				}
				wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
				wget.download();
				imagepanel.setImage(new File(wget.getPostOutput()));
				imagepanel.repaint();
			}
		});
		
		//Zoom Out
		zoom_out.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if (map.getMess_M() != 0){
					if (map.getZoomlevel() < map.getMaxZoomLevel()){
						if(map.getProvider().equals("Google")){
							map.setZoom(map.getZoomlevel()+1, "Google");
						}else if (map.getProvider().equals("Expedia")){
							map.setZoom(map.getZoomlevel()+1, "Expedia");
						}
					}
				}
				wget.setOutput("map_"+"Google"+"_"+map.getLatitude()+"_"+map.getLongitude()+"_"+map.getZoom());
				wget.download();
				imagepanel.setImage(new File(wget.getPostOutput()));
				imagepanel.repaint();
			}
		});
		
		//Download der Map
		downmap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if (onlywindow.getFlag() != true){
					onlywindow.setFlag(true);
					GuiDown down = new GuiDown(map,calc,imagepanel,onlywindow);
				}
			}
		});
		
		//Exit Button
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				System.exit(0);
			}
		});
		
		//Waypoints auf Gerät übetragen
		info.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				JOptionPane.showMessageDialog(null,"OHGARMIN - Version 1.0, 10. Juli 2008, Christoph Ott und Georg Haßlinger","Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		
		//Waypoints auf Gerät übetragen
		garmin_save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if (count_wp_user>0)
				{
					waypoints.saveWayPoints(ApiManager);
					JOptionPane.showMessageDialog(null,"Wegpunkte wurden übertragen!","Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
				}
				else
			    	JOptionPane.showMessageDialog(null,"Es wurden noch keine Wegpunkte angelegt!","Fehler", JOptionPane.ERROR_MESSAGE);					
			}
		});
		
		chose_pic_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){				   
				Frame SaveMainFrame = new Frame(); 				
				FileDialog OpenDialog = new FileDialog(SaveMainFrame,"Ordner aussuchen",FileDialog.LOAD); 
				OpenDialog.setDirectory(FotoPath);
				OpenDialog.setModal(true); 
				OpenDialog.setVisible(true); 
				FotoPath = OpenDialog.getDirectory(); 
				
				pathset = true;
				
				//System.out.println("Filepath: " + FotoPath);
			}
		});
		
		//Wegpunkt-Modus
		waypoint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if (waypointflag == false){
					Cursor c = new Cursor(Cursor.CROSSHAIR_CURSOR);
					HauptGui.this.frame.setCursor(c);
					waypointflag = true;
					waypoints.setMap(map);
					waypoints.setCalculate(calc);
					imagepanel.setWegPunkte(waypoints);
				}else{
					Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
					HauptGui.this.frame.setCursor(c);
					waypointflag = false;
				}
			}
		});
		
		//Mousehandler
		imagepanel.addMouseMotionListener(this);
		imagepanel.addMouseListener(this);
	}
	
	//Main Methode
	public static void main (String[] args){
		HauptGui gui = new HauptGui();
		gui.guiCreate();
	}
	
	 
	//Keyboard Events
	
	public void actionPerformed(ActionEvent arg0) {
		
	}
	
	//Mouse Events

	public void mouseClicked(MouseEvent arg0) {
		if (waypointflag == true){
			waypoints.addWayPoint(calc.PixelToGradY(calc.DistanzToMidY_Pixel(arg0.getY())), calc.PixelToGradX(calc.DistanzToMidX_Pixel(arg0.getX())));
			count_wp_user++;
			imagepanel.repaint();
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void loadFotos()
	{
		if (pathset)
		{
			
			File dir = new File(FotoPath); //Fotos aus ordner FotoPath werden gelesen
			
			FotoObject foto; //fotoObject, das nacher gespeichert wird
			Random rX= new Random(); 
			BufferedImage image; 		
			File[] f = dir.listFiles();
			WegPunkte wayp = new WegPunkte(); 	//Fotos werden als WegPunkte angelegt
			
			wayp.setMap(map);					//Den Fotos wird eine map zugewiesen (f�r Berechnungen)
			wayp.setCalculate(calc);			//Ebenso ein Calc-Objekt
			
			if (!ApiManager.isStarted())
				ApiManager.startApi(); //startet den API-Manager
			if (!ApiManager.hasWpReceived())
			ApiManager.getWaypoints();
			
		    if (f != null)  
		    	for (int i = 0; i < f.length; i++) //geht den Ornder durch (für alle Fotos)
		        {				
				foto = new FotoObject(); //Speicher holen für foto

				image = null; 
				
				try{
					System.out.println("f: " + f[i].getName()+ " "+f[i].getAbsolutePath());
					image = ImageIO.read(f[i]); //Foto wird aus Datei eingelesen ins image
				}catch(Exception ex){ System.out.println("Fehler beim Lesen der Fotos");
				System.out.println("1" + ex.getStackTrace().toString());
				System.out.println("2" + ex.getMessage());
				System.out.println("3" + ex.toString());}
				
						
				foto.setImage(image); //Dieses Image wird dem FotoObject zugewiesen
				
				//System.out.println("last mod:" + f[i].lastModified()/1000);
				
				foto.setTimestamp((f[i].lastModified())/1000); //der Zeistempel wird gespeichert
				//System.out.println("get timestamp:" + foto.getTimestamp());
				foto = getFotoData(foto); //GPS-Koordinaten anhand der Wegpunkte holen
					
				imagepanel.setWegPunkteImage(wayp); //diese Wegpunkte werden dem Imagepanel zugewiesen
				wayp.addWayPointImage(foto);		//das aktuelle Foto wird hinzugefügt
				
				FotoCounter++;
		       }
		    
		    if (count_wp_fotos == 0)	    	
		    	JOptionPane.showMessageDialog(null,"Es wurden keine passenden Waypoints gefunden!","Fehler", JOptionPane.ERROR_MESSAGE);
	
		    imagepanel.repaint();
		}
		else
			JOptionPane.showMessageDialog(null,"Sie müssen erst einen Pfad auswählen!","Fehler", JOptionPane.ERROR_MESSAGE);
			
	}	
	
	public String format(double x)
	{
		java.text.DecimalFormat df = new java.text.DecimalFormat("###.####"); 
		return df.format(x);
	}
	
	public FotoObject getFotoData(FotoObject Foto)
	{//Hier werden die XY-GPS-Koordinaten aus dem Gerät gelesen
		double distanz;
		double diftime=0; //zeitunterschied zwischen foto und letztem waypoint davor
		long prevWPTime, nextWPTime;
		double prevWPX, nextWPX, prevWPY, nextWPY;
		double a,b;
		double newX=0, newY=0;
		double teil_a,teil_b;
		FotoDate fd = new FotoDate(1,1,1970); //Datum für fotos
		long GarminToFileTime = (fd.getMS(1,1,1970,1,1,1990))/1000;
		//GarminToFileTime=GarminToFileTime-86400;
		
		long MS_since_1990, MS_since_prevWP;
		boolean turna=false,turnb=false;
		double weg_a,weg_b, wegX=0,wegY=0;
		
		long FTS = Foto.getTimestamp()+86400;

		prevWPTime=ApiManager.getpreviosWPTime(FTS);
		System.out.println("prevWPTime:" + prevWPTime);
		
		nextWPTime=ApiManager.getnextWPTime(FTS);
		System.out.println("nextWPTime:" + nextWPTime);
		
		prevWPX=ApiManager.getpreviosWPX(FTS);
			
		prevWPY=ApiManager.getpreviosWPY(FTS);
		
		nextWPX=ApiManager.getnextWPX(FTS);
		
		nextWPY=ApiManager.getnextWPY(FTS);

		
		if (prevWPX!=-1 && prevWPY!=-1 && nextWPX!=-1 && nextWPY!=-1) count_wp_fotos++;
		
		//System.out.println("Count: "+count_wp_fotos);
		
		prevWPX=prevWPX*Garmin_GPS_format;		prevWPY=prevWPY*Garmin_GPS_format;
		nextWPX=nextWPX*Garmin_GPS_format;		nextWPY=nextWPY*Garmin_GPS_format;
		
		System.out.println("prevWPX:" + prevWPX);
		System.out.println("prevWPY:" + prevWPY);
		System.out.println("nextWPX:" + nextWPX);
		System.out.println("nextWPY:" + nextWPY);
		
		//Lineare Interpolation folgt:
		
		diftime=nextWPTime-prevWPTime;
		if(diftime<0) diftime=diftime*-1; //Nur zur sicherheit
		
		a= nextWPX-prevWPX; 		if (a<0) {turna=true;a=a*-1;}
		//a=calc.GradToMeterX(a);
		// X-Abstand
		b= nextWPY-prevWPY; 		if (b<0) {turnb=true;b=b*-1;}
		//b=calc.GradToMeterY(b);
		System.out.println("a in grad: " + a+ " , b in grad: " +b);
		// Y-Abstand
		
		MS_since_1990 = ((FTS-GarminToFileTime)); //MS seit 1990
		MS_since_prevWP=(MS_since_1990-prevWPTime); //zeit die seit dem letzten wegpunkt vergangen is
		
		if(MS_since_prevWP<0) MS_since_prevWP=MS_since_prevWP*-1; //falls negativ-> umdrehen!
		System.out.println("ms seit letztem wp: " + MS_since_prevWP);
		
		weg_a=(a/diftime)*MS_since_prevWP; //weg der bis zum foto wirklich zurückgelegt wurde
		teil_a = weg_a/a;		//anteil an der distanz der Wegpunnkte
		System.out.println("weg_a: " + weg_a + " , teil_a: "+teil_a+" , a: " +a);
		
		weg_b=(b/diftime)*MS_since_prevWP; //weg der bis zum foto wirklich zurückgelegt wurde
		teil_b = weg_b/b;		//anteil an der distanz der Wegpunnkte
		System.out.println("weg_b: " + weg_b + " , teil_b: "+teil_b+" , b: " +b);
		
		wegX = a*teil_a; if (turna) wegX = wegX *-1; //dieser anteil wird für X und Y
		wegY = b*teil_b; if (turnb) wegY = wegY *-1; //zur anfangskoordinate hinzugefügt
	
		//wegX
		System.out.println("wegx: " +wegX+ ", wegy: " +wegY);
		//wegX=calc.MeterToGradX(wegX);
		//wegY=calc.MeterToGradY(wegY);
		
		//System.out.println("wegx: " +wegX+ ", wegy: " +wegY);
		
		newX = prevWPX+wegX;//(prevWPX-map.getLongitude()); //+wegX; //die Koordinaten fürs Foto werden ermittelt
		newY = prevWPY+wegY;//(prevWPY-map.getLatitude());				
		
		System.out.println("newx: " + newX + ", newy: " + newY);
		Foto.setGPSX(newX);
		Foto.setGPSY(newY);				
		
	    return Foto; 
	}
	
	public void mouseMoved(MouseEvent arg0) {
		imagepanel.checkResize(arg0.getX(),arg0.getY());
		//Gibt bei jedem Mouse Move die aktuelle Lat und Long Werte aus
		//Es wird zuerst der Abstand gemessen in Pixeln
		//dann werden die Pixeln in Grad umgewandelt
		//und als letztes in eine nette Ausgabeform gebracht
		HauptGui.this.statusbar.setText("Long: "+calc.WertFormatieren(calc.PixelToGradX
				(calc.DistanzToMidX_Pixel((arg0.getX()))))+" | Lat: "+calc.WertFormatieren
				(calc.PixelToGradY(calc.DistanzToMidY_Pixel((arg0.getY())))));
	}
}
