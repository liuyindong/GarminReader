package ohgarmin;
/* GuiDown - Klasse
 * Bakk-Arbeit für Prof. Frank
 * von Georg Haßlinger und Christoph Ott
 * 
 * Diese Klasse erzeugt ein neues Gui.
 * Mit dieser GUI kann man von Google Maps Karten herunterladen
 * 
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
public class GuiDown implements ActionListener{
	JFrame frame2;
	JPanel panel1;
	JLabel label_lat;
	JTextField edit_lat;
	JLabel label_long;
	JTextField edit_long;
	JLabel label_zoom;
	JComboBox cbx_zoom;
	JButton btn_fertig;
	JLabel label_error;
	JRadioButton radio_google;
	JRadioButton radio_expedia;
	JPanel panel_check;
	ButtonGroup bg;
	int mapprovider = 1;
	Karte mapdown;
	ImagePanel imagepanel;
	Calculate calculate;
	OnlyWindow onlywindowflag;
	//Der Konstruktor braucht die Objekte ImagePanel und Wget
	public GuiDown(Karte map2, Calculate calc2,ImagePanel imagepanel2,OnlyWindow onlywindow2){
		calculate = calc2;
		mapdown = map2;
		imagepanel = imagepanel2;
		onlywindowflag = onlywindow2;
		//Erzeugen des GUI
		frame2 = new JFrame("Download a map");
		panel1 = new JPanel();
		label_lat = new JLabel("Latitude (Bsp: 48.000000)       ");
		edit_lat = new JTextField(20);
		edit_lat.setText("48.1705");
		label_long = new JLabel("Longitude (Bsp: 16.000000)    ");
		edit_long = new JTextField(20);
		edit_long.setText("16.3847");
		label_zoom = new JLabel("Zoom (0-9) (0 = ganz Nahe & 9 = ganz weit weg)         ");
		cbx_zoom = new JComboBox();
		btn_fertig = new JButton("Fertig");
		label_error = new JLabel("");
		panel_check = new JPanel();
		//RadioButton erzeugen
		bg = new ButtonGroup();
		radio_google = new JRadioButton("Google Maps");
		radio_expedia = new JRadioButton("Expedia");
		bg.add(radio_google);
		bg.add(radio_expedia);
		//Auf dem Panel liegen alle Komponeten
		//Panel wird ins Zentrum vom Frame geaddet
		frame2.getContentPane().add(BorderLayout.CENTER,panel1);
		//Adden aller Komponenten auf das Panel
		panel1.add(label_lat);
		panel1.add(edit_lat);
		panel1.add(label_long);
		panel1.add(edit_long);
		panel1.add(label_zoom);
		panel1.add(cbx_zoom);
		panel1.add(panel_check);
		panel_check.setLayout(new BoxLayout(panel_check,BoxLayout.Y_AXIS));
		//panel_check.add(radio_google);
		//panel_check.add(radio_expedia);
		//Starteinstellungen für den Radiobuttons 
		radio_google.setSelected(true);
		this.setComboBox(cbx_zoom, 1);
		panel_check.add(btn_fertig);
		panel1.add(label_error);
		frame2.setSize(500,200);
		frame2.setVisible(true);
		frame2.setResizable(false);
		btn_fertig.setMnemonic(java.awt.event.KeyEvent.VK_F);
		
		//Damit man wieder eine Instanz hinbekommt muss das Hilfsflag auch auf False gesetzt
		// werden sobald man auf das X klickt
		frame2.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt){
				onlywindowflag.setFlag(false);
				
			}
		});
		
		//OnClick Ereignis von den RadioButtons
		radio_google.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				GuiDown.this.setComboBox(GuiDown.this.cbx_zoom,1);
				mapprovider = 1;
			}
		});
		radio_expedia.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				GuiDown.this.setComboBox(GuiDown.this.cbx_zoom,2);
				mapprovider = 2;
			}
		});
		
		//OnClick Ereignis vom Button
		btn_fertig.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//neue Karte wird erstellt
				mapdown.setLatitude(Double.parseDouble(GuiDown.this.edit_lat.getText()));
				mapdown.setLongitude(Double.parseDouble(GuiDown.this.edit_long.getText()));
				mapdown.setWidth(600);
				mapdown.setHeight(400);
				Wget download = new Wget(mapdown);
				if (mapprovider == 1){
					mapdown.setZoom(Integer.parseInt(GuiDown.this.cbx_zoom.getSelectedItem().toString()),"Google");
					download.setOutput("map_"+"Google"+"_"+mapdown.getLatitude()+"_"+mapdown.getLongitude()+"_"+mapdown.getZoom());
				}else if (mapprovider == 2){
					mapdown.setZoom(Integer.parseInt(GuiDown.this.cbx_zoom.getSelectedItem().toString()),"Expedia");
					download.setOutput("map_"+"Expedia"+"_"+mapdown.getLatitude()+"_"+mapdown.getLongitude()+"_"+mapdown.getZoom());
				}
				
				//OnlyWindow
				onlywindowflag.setFlag(false);
				
				//nun wird die map downgeloadet
				download.download();
				
				//eine neue Calcklasse wird erzeugt
				Calculate calc = new Calculate(mapdown);
				calculate = calc;
				frame2.setVisible(false);
				//Bild wird geladen
				imagepanel.setImage(new File(download.getPostOutput()));
				//Bild neugezeichnet
				imagepanel.repaint();
			}
		});
	}
	
	//Kontrollfunktionen
	public boolean checklat(String lat){
	
		return true;
	}
	
	public boolean checklong(String longi){
		return true;
	}
	
	//Erstellt die ComboBoxeinträge für den jeweils ausgewählten MapProvider
	public void setComboBox(JComboBox cbx_foo,int mapprovider){
		//Zuerst werden alle gelöscht
		cbx_foo.removeAllItems();
		switch (mapprovider){
			//Google Maps hat Zoom von 0-9
			case 1:{ 
				for (int x=0;x <10;x++){
					cbx_foo.addItem(""+x);
					label_zoom.setText("Zoom (0-9) (0 = ganz Nahe & 9 = ganz weit weg)         ");
				}
				break;	
			}
			//Expedia von 0-10
			case 2:{
				for (int x=0;x <11;x++){
					cbx_foo.addItem(""+x);
					label_zoom.setText("Zoom (0-9) (0 = ganz Nahe & 10 = ganz weit weg)         ");
				}				
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
