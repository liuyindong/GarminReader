
package ohgarmin;
public interface Int_Wget {
	//Setter
	//*************************
	//setzt den Dateinamen mit dem Ordner zusammen
	//Bsp: maps/datei.jpg
	public void setOutput(String output);
	
	//Setzt die Latitude und Longitude auf spezielle Formatierte Werte
	public String setGoogleString(double Wert);
	public String setExpediaString(double Wert);
	//Get
	//**************************
	//gibt die gesamte Url aus von der man downloadet
	public String getWholeUrlGoogle();
	public String getWholeUrlExpedia();
	
	//gibt den Dateinamen aus = beispiel
	public String getOutput();
	//gibt den Dateinamen + Postfix aus = beispiel.jpg 
	public String getPostOutput();
	
	//Methoden - Funktionen
	//**************************
	//checkt ob ein Ordner schon existiert, wenn nicht wird einer erstellt
	public void dirCheck(String dirname);
	//checkt ob eine Dateiname existiert und gibt true oder false zurück
	public boolean fileCheck(String dateiname);
	//Methode mit der man die Karten downloadet
	public void download();
}
