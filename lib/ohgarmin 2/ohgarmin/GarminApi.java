package ohgarmin;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbIrp;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;

public class GarminApi {
	
	//Konstanten um das Gerät zu identifizieren
	private static short idVendor = 2334;
	private static short idProduct = 3;
	//Konstanten vom Gerät die für die Pipes zuständig sind
	private static int bulkIn = 0;
	private static int bulkInterrupt = 1;
	private static int bulkOut = 2;
	//Vector um mehre Geräte zu identifzieren und zu speichern
	private DeviceList liste = new DeviceList();
	private UsbInterface interf;
	
	//Die 3 Pipes
	private UsbPipe pipeIn = null;
	private UsbPipe pipeInterrupt = null;
	private UsbPipe pipeOut = null;
	
		//Konstruktor
		public GarminApi(){
			this.initUsb();
			//Benutze 1 Element aus der Liste
			this.useUsbDevice(1);
		}
		
		//Methode holt sich das USBDevice nach dem gesucht wird
		public void initUsb(){
			UsbHub usb = getUsbHub();
			//Finde alle Usbgeräte
			findDevices(usb);
			
		}
		
		//Methode um ein Device auszusuchen von dem Vecotr wo alle DEvices gespeichert sind
		public void useUsbDevice(int nummer){
			try{
				getPipes(liste.getUsbDevice(nummer-1));
			}catch(ArrayIndexOutOfBoundsException ex){
				System.out.println("Nummer des Devices ist nicht gültig");
			}
		}
		
		//Methode um den RootHub zu bekommen
		public UsbHub getUsbHub(){
			UsbServices services;
			try {
				services = UsbHostManager.getUsbServices();
				UsbHub vroothub = services.getRootUsbHub();
				return vroothub;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (UsbException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		//Bekommt einen UsbHub und findet die dranhängenden Devices
		public void findDevices(UsbHub hub){
			List Devices = hub.getAttachedUsbDevices();
		    Iterator iterator = Devices.iterator();
		    while (iterator.hasNext()) {
		    	UsbDevice device = (UsbDevice) iterator.next();
		    	
	        	//falls Gerät ein Hub ist dann bitte nochmals rekurisv
	        	// durchgehn
		        if (device.isUsbHub()) {
		          findDevices((UsbHub) device);
		        }else{
		        	UsbDeviceDescriptor descriptor = null;
			    	try{
		        		descriptor = device.getUsbDeviceDescriptor();
		        	}catch(Exception ex){
		        		ex.printStackTrace();
		        	}
		        	if (descriptor.idProduct() == idProduct && descriptor.idVendor() == idVendor){
		        		//Erzeuge ein neues Device Objekt
		        		//setze gleich die notwendigen Daten
		        		Device geraet = new Device();
		        		geraet.setName("Garmin eTrex Vista HCx");
		        		geraet.setBulkIn(bulkIn);
		        		geraet.setBulkInterrupt(bulkInterrupt);
		        		geraet.setBulkOut(bulkOut);
		        		geraet.setUsbDevice((UsbDevice) device);
		        		//Füge es in die Device Liste
		        		liste.addDevice(geraet);
		        	}
		        }
		      } 
		}
		
		//Erstellt die notwendigen Pipes + erstellt eine Policy
		public void getPipes(UsbDevice device){
			try{
				device.getActiveUsbConfiguration();
				UsbConfiguration config = device.getActiveUsbConfiguration();
				interf = config.getUsbInterface((byte)0);
				interf.claim(new UsbInterfacePolicy() {
					public boolean forceClaim(UsbInterface usbInterface) {
					        return true;
					}
				});
				List totalEndpoints = interf.getUsbEndpoints();
				//Endpoints
				UsbEndpoint pipeInEndP = (UsbEndpoint) totalEndpoints.get(bulkIn);
				UsbEndpoint pipeOutEndP = (UsbEndpoint) totalEndpoints.get(bulkOut);
				UsbEndpoint pipeInterruptP = (UsbEndpoint) totalEndpoints.get(bulkInterrupt);
				
				//Pipes
				pipeIn = pipeInEndP.getUsbPipe();
				pipeOut = pipeOutEndP.getUsbPipe();
				pipeInterrupt = pipeInterruptP.getUsbPipe();
			
			}catch(NullPointerException ex){
				System.out.println("Kein Gerät gefunden");
				System.exit(0);
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
	    }
		
		//Wichtigste Methode für die API - hier findet die Kommunikation statt mit dem Gerät
		//Die Parameter geben an welche Pipe man beobachten will, welches Packet man senden will
		//+ ob man eine Pipe wirklich die Ausgabe einer Pipe beobachten will - bei
		//manchen Funktionen ist das unerwünscht BSP: PowerOff
		public Vector send(UsbPipe pipeAnswer, byte[] packet,boolean DeviceAnswer){
			Vector<byte[]> answer = new Vector <byte[]> ();
			try{
				if (! pipeOut.isOpen()){
					pipeOut.open();
				}
				if (!pipeAnswer.isOpen()){
					pipeAnswer.open();
				}
				//Sende Signal
				UsbIrp irpSend = pipeOut.createUsbIrp();
				UsbIrp irpAnswer = pipeAnswer.createUsbIrp();
				irpSend.setData(packet);
				
				byte[] empfang = new byte[255];
				//Sende solange bis das Ergebnis nicht null ist
				//Das Device reagiert manachmal nicht wie erwartet
				//deswegen muss man das so kontrollieren
				//Ein Counter rennt mit - es wird 2 mal probiert was zu senden
				int counter = 0;
				do{
					irpSend.setComplete(false);
					pipeOut.asyncSubmit(packet);
					irpSend.waitUntilComplete(1000);
					//System.out.println("DebugIn: "+packet[4]);
					
					//Falls man keine Antwort empfangen will (bsp: PowerOff)
					if (DeviceAnswer == true){
						empfang = new byte[255];
						irpAnswer.setComplete(false);
						pipeAnswer.asyncSubmit(empfang);
						irpAnswer.waitUntilComplete(1000);
						//System.out.println("Debug: "+empfang[4]);
						if (empfang[4] != (byte) 0x00){
							//System.out.println("Empfang: "+empfang);
							answer.add(empfang);
						}
					}
					counter ++;
					//System.out.println("Counter: "+counter);
				}while(DeviceAnswer == true && ((empfang[4] == (byte)0x00) && (counter < 2)));
				
				//Empfange solange bis nur noch nuller kommen + ein kleiner counter
				int counter2 = 0;
				while(((empfang[4] != (byte) 0x00) && DeviceAnswer == true && counter < 2) || (DeviceAnswer == true && counter2 < 2) ){
					if (DeviceAnswer == true){
						empfang = new byte[255];
						irpAnswer.setComplete(false);
						pipeAnswer.asyncSubmit(empfang);
						irpAnswer.waitUntilComplete(1000);
						//System.out.println("Debug2: "+empfang[4]);
						if (empfang[4] != (byte) 0x00){
							//System.out.println("Empfang2: "+empfang);
							answer.add(empfang);
						}
					}
					counter2++;
				}
				irpAnswer.setComplete(true);
				irpSend.setComplete(true);
				pipeOut.abortAllSubmissions();
				pipeAnswer.abortAllSubmissions();
				pipeOut.close();
				pipeAnswer.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			/*
			//Debug
			for (int g = 0; g < answer.size(); g++){
				byte[] debug = (byte[]) answer.elementAt(g);
				for (int h = 0; h < debug.length; h++){
					System.out.print(debug[h]+" ");
				}
				System.out.println();
			}
			*/
			return answer;
		}
		
		//Sendet das Start_Session-Packet
		//dies braucht man vor jeder Übertragung
		private void Start_Session(Packet packet){
			byte[] session = packet.createPacket(packet.Pid_Start_Session, new byte[0]);
			send(pipeInterrupt,session,true);
		}
		
		//Liefert ProduktDaten
		private Vector getAllProductProtocolData(){
			Packet ProductData = new Packet();
			//Schickt Session_Started_Packet
			this.Start_Session(ProductData);
			byte[] packet = ProductData.createPacket(ProductData.Pid_Product_Rqst, new byte[0]);
			//Util.showByteCode(packet);
			Vector ausgabe = send(pipeIn,packet,true);
			//System.out.println("Debug-Product: "+ausgabe);
			return ausgabe;
		}
		
		//Liefert einen String mit allen ProduktDaten
		public String getProductData(){
			//Sollte einen Vector mit 3 Elementen liefern
			Vector all = this.getAllProductProtocolData();
			byte[] antwort = (byte[]) all.elementAt(0);
			
			// Debug
			/*
			for (int g = 0; g < antwort.length; g++){
				System.out.print(antwort[g]+" ");	
			}
			System.out.println();
			*/
			
			int anzahl = 0;
			//Größe holen
			if ((int) antwort[8] < 0) {
				
				anzahl = 128 + (int) antwort[8];
			}else{
				anzahl = (int) antwort[8];
			}
			System.out.println(anzahl);	
			
			//Nur die Datenpakte holen
			byte[] data = new byte[anzahl];
			for (int x = 0; x < anzahl; x++){
				data[x] = antwort[x+12]; 
			}
			//Null-terminated Strings finden
			int nullterm = 0;
			for (int x=4; x < anzahl && data[x] != 0; x++){
				nullterm ++;
			}
			//Umwandlung von byte in short
		
			int erg = Util.byteToShort(data,0);
			int erg2 = Util.byteToShort(data, 2);
			
			//Umwandlung von byte in String
			String erg3 = new String (data,4,nullterm);
			
			return "Device Id: "+erg+"\nDevice Version: "+erg2+"\nProduct Description: "+erg3;
			
		}
		
		//Methode um die Protocol Daten zu erhalten
		public Vector getProtocolData(){
			Vector all = this.getAllProductProtocolData();
			Vector <String> antwort = new Vector<String>();
			//Sollte das 3 Element sein im Vektor
			byte[] antwortByte = (byte[]) all.elementAt(2);
			
			//hole die Größe des Packetes
			int sizee = antwortByte[8];
			if (sizee < 0){
				sizee = sizee + 256;
			}
			//Formatiere die Informationen
			//1 Byte = Tag
			//2-3 Byte = Data
			//Speicher die Daten als String ab
			//füge alles in einen Vektor ein
			for (int y = 0; y < sizee; y++){
				if (y % 3 == 2){
					char data = (char)antwortByte[y+10];
					int in = ((antwortByte[y+12] & 0xff) << 8) + (antwortByte[y+11]& 0xff);
					String prot = "Tag: "+data+" "+"Data: "+in;
					antwort.add(prot);
				}
			}
			return antwort;
		}
		
		//Methode um ein Gerät abzuschalten
		public void TurnOffPower(){
			Packet TurnOff = new Packet();
			this.Start_Session(TurnOff);
			byte[] cmnd = Util.shortTobyte(TurnOff.Cmnd_Turn_Off_Pwr);
			byte[] packet = TurnOff.createPacket(TurnOff.Pid_Command_Data,cmnd);
			send(pipeIn,packet,false);
		}
		
		//Methode um WayPointss zu bekommen
		public Vector getWayPoints(){
			Vector<WayPoints> WayPointsList = new Vector<WayPoints>();
			Packet WayPoints = new Packet();
			this.Start_Session(WayPoints);
			byte[] cmnd = Util.shortTobyte(WayPoints.Cmnd_Transfer_Wpt);
			byte[] packet = WayPoints.createPacket(WayPoints.Pid_Command_Data,cmnd);
			Vector antwort = send(pipeIn,packet,true);
			
			//System.out.println("WegPunkte");
			
			for (int x = 0; x < antwort.size()-1; x ++){
				//Datentyp D110
				byte[] antwortByte = (byte[]) antwort.elementAt(x);
				WayPointsList.add(new WayPoints(antwortByte));
			}
			
			return WayPointsList;
		}
		
		//Um Transfers zu beenden
		private void AbortTransmission(){
			Packet Abort = new Packet();
			this.Start_Session(Abort);
			byte[] cmnd = Util.shortTobyte(Abort.Cmnd_Abort_Transfer);
			byte[] packet = Abort.createPacket(Abort.Pid_Command_Data,cmnd);
			send(pipeIn,packet,false);
		}
		
		//Sendet einen WegPunkt an das Gerät
		//Mehtode um eine Map upzuLoaden
		private void sendWayPoints(byte[] b){
			Packet WayPoints = new Packet();
			this.Start_Session(WayPoints);
			byte[] packet = WayPoints.createPacket(WayPoints.Pid_Records, new byte[0]);
			
			byte[] packet2 = WayPoints.createPacket(WayPoints.Pid_Wpt_Data, b);

			byte[] packet3 = WayPoints.createPacket(WayPoints.Pid_Xfer_Cmplt, new byte[0]);
			
			//System.out.println("Send Pid_REcord");
			//System.out.println("Debug: Pid");
			//Util.showByteCode(packet);
			send(pipeInterrupt,packet,true);
			//System.out.println("Send Wpt");
			//System.out.println("Debug Wpt: ");
			send(pipeIn,packet2,false);
			//System.out.println("Send Xfer");
			//System.out.println("Debug Xfer: ");
			//Util.showByteCode(packet3);
			send(pipeInterrupt,packet3,true);

		}
		
		//Methode um einen WegPunkt zu erstellen -> dieser wird dann upgeloadet
		//latitude & longitude muss als semicircle angegeben werden
		public void createWayPoint(char[] id, double latitude, double longitude){
			WayPoints newWayPoints = new WayPoints();
			//Latitude & Longitude(wird in SemiCircles übernommen)
			newWayPoints.setLatitude(latitude);
			newWayPoints.setLongitude(longitude);
			//ident
			newWayPoints.setIdent(id);
			//comment
			char[] comment = new char[0];
			newWayPoints.setComment(comment);
			int anzahl = (74 + id.length + comment.length);
			//generiert aus den bisherigen Daten ein BytePacket
			sendWayPoints(newWayPoints.createBytePacket(anzahl));
		}	
		
		//Methode um TrackRouten zu bekommen
		public Vector getTrackRoute(){
			System.out.println("Starte TrackListe");
			Vector <TrackListe> RouteTrackListe = new Vector<TrackListe>();
			Packet RouteTrack = new Packet();
			this.Start_Session(RouteTrack);
			byte[] cmnd = Util.shortTobyte(RouteTrack.Cmnd_Transfer_Trk);
			byte[] packet = RouteTrack.createPacket(RouteTrack.Pid_Command_Data,cmnd);
			//System.out.println(packet);
			Vector antwort = send(pipeIn,packet,true);
			int index = -1;
			System.out.println("WegPunkte");
			for (int x = 0; x < antwort.size(); x ++){
				//Datentyp D110
				byte[] antwortByte = (byte[]) antwort.elementAt(x);
				Util.showByteCode(antwortByte);
				if (antwortByte[4] == 99){
					index ++;
					RouteTrackListe.add(new TrackListe(antwortByte));
				}else if (antwortByte[4] == 34){
		//	System.out.println(index);
					TrackListe tList = (TrackListe) RouteTrackListe.elementAt(index);
					tList.add(new Track(antwortByte));
				}
			}
			
		/*	//Zum Debuggen
			for (int x=0; x < RouteTrackListe.size(); x++){
				TrackListe tList = (TrackListe) RouteTrackListe.elementAt(x);
				tList.toString();
					for (int y = 0; y < tList.getSize(); y++){
					Track tr = (Track)tList.getTrack(y);
					System.out.println(tr.toString());
				}
			}
		*/
			return RouteTrackListe;
		}
}
