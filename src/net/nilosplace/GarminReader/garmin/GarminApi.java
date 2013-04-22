package net.nilosplace.GarminReader.garmin;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbServices;

import net.nilosplace.GarminReader.garmin.queue.GarminInterruptQueue;
import net.nilosplace.GarminReader.garmin.queue.GarminRecievingQueue;
import net.nilosplace.GarminReader.garmin.queue.GarminSendingQueue;

public class GarminApi {
	
	private UsbDevice garminDevice = null;
	
	private GarminSendingQueue send = null;
	private GarminRecievingQueue read = null;
	private GarminInterruptQueue rupt = null;

	public GarminApi() {
		try {
			UsbServices services = UsbHostManager.getUsbServices();
			
			UsbHub roothub = services.getRootUsbHub();
			GarminDevice device = findDevices(roothub, 0);
			if(device != null) {
				System.out.println("Device found: " + device);
				initDevice(device);
			} else {
				System.out.println("Error Garmin Device Could not be found");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (UsbException e) {
			e.printStackTrace();
		}
	}

	public GarminDevice findDevices(UsbHub hub, int level){
		List<UsbDevice> Devices = hub.getAttachedUsbDevices();
		System.out.println(hub);
		for(UsbDevice device: Devices) {
			System.out.println(device);
			if (device.isUsbHub()) {
				findDevices((UsbHub) device, level + 1);
			} else{
				UsbDeviceDescriptor descriptor = null;
				try{
					descriptor = device.getUsbDeviceDescriptor();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//System.out.println(descriptor.idProduct());
				//System.out.println(descriptor.idVendor());
				
				if (descriptor.idProduct() == GarminDevice.idProduct && descriptor.idVendor() == GarminDevice.idVendor) {
					//return new GarminDevice(device);
				}
			}
		}
		return null;
	}

	public void initDevice(GarminDevice device){
		try{
			device.getDevice().getActiveUsbConfiguration();
			UsbConfiguration config = device.getDevice().getActiveUsbConfiguration();

			UsbInterface interf = config.getUsbInterface((byte)0);
			interf.claim(new UsbInterfacePolicy() {
				public boolean forceClaim(UsbInterface usbInterface) {
					return true;
				}
			});

			List<UsbEndpoint> totalEndpoints = interf.getUsbEndpoints();
			
//			for(UsbEndpoint e: totalEndpoints) {
//				System.out.println(e.getUsbEndpointDescriptor());
//			}
			
			UsbEndpoint pipeInEndP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkIn);
			UsbEndpoint pipeOutEndP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkOut);
			UsbEndpoint pipeInterruptP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkInterrupt);
			
//			System.out.println(pipeInEndP.getType());
//			System.out.println(pipeOutEndP.getType());
//			System.out.println(pipeInterruptP.getType());
//			
//			System.out.println(pipeInEndP.getDirection());
//			System.out.println(pipeOutEndP.getDirection());
//			System.out.println(pipeInterruptP.getDirection());
			
			send = new GarminSendingQueue(pipeOutEndP);
			read = new GarminRecievingQueue(pipeInEndP);
			rupt = new GarminInterruptQueue(pipeInterruptP);

			
		}catch(NullPointerException ex){
			System.out.println("Null Pointer: " + ex.getMessage());
			//ex.printStackTrace();
			System.exit(0);
			
		} catch(Exception ex){
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.exit(0);
		}
	}
		
	public void send(Packet packet){
		send.send(packet.createPacket());
	}
	
	public Packet recieve() {
		return read.recieve();
	}

	public void startSession() {
		send(new Packet(Packet.Pid_Start_Session));
	}
		
	public void getAllProductProtocolData(){
		send(new Packet(Packet.Pid_Product_Rqst));
	}
		
//	public String getProductData(){
//		//Sollte einen Vector mit 3 Elementen liefern
//		Vector all = getAllProductProtocolData();
//		byte[] antwort = (byte[]) all.elementAt(0);
//		
//		// Debug
//		
//		for (int g = 0; g < antwort.length; g++){
//			System.out.print(antwort[g]+" ");	
//		}
//		System.out.println();
//		
//		
//		int anzahl = 0;
//		if ((int) antwort[8] < 0) {
//			
//			anzahl = 128 + (int) antwort[8];
//		}else{
//			anzahl = (int) antwort[8];
//		}
//		System.out.println(anzahl);	
//
//		byte[] data = new byte[anzahl];
//		for (int x = 0; x < anzahl; x++){
//			data[x] = antwort[x+12]; 
//		}
//		//Null-terminated Strings finden
//		int nullterm = 0;
//		for (int x=4; x < anzahl && data[x] != 0; x++){
//			nullterm ++;
//		}
//		//Umwandlung von byte in short
//	
//		int erg = Util.byteToShort(data,0);
//		int erg2 = Util.byteToShort(data, 2);
//		
//		//Umwandlung von byte in String
//		String erg3 = new String (data,4,nullterm);
//		
//		return "Device Id: "+erg+"\nDevice Version: "+erg2+"\nProduct Description: "+erg3;
//		
//	}
//		
//		//Methode um die Protocol Daten zu erhalten
//		public Vector getProtocolData(){
//			Vector all = this.getAllProductProtocolData();
//			Vector <String> antwort = new Vector<String>();
//			//Sollte das 3 Element sein im Vektor
//			byte[] antwortByte = (byte[]) all.elementAt(2);
//			
//			//hole die Gr����e des Packetes
//			int sizee = antwortByte[8];
//			if (sizee < 0){
//				sizee = sizee + 256;
//			}
//			//Formatiere die Informationen
//			//1 Byte = Tag
//			//2-3 Byte = Data
//			//Speicher die Daten als String ab
//			//f��ge alles in einen Vektor ein
//			for (int y = 0; y < sizee; y++){
//				if (y % 3 == 2){
//					char data = (char)antwortByte[y+10];
//					int in = ((antwortByte[y+12] & 0xff) << 8) + (antwortByte[y+11]& 0xff);
//					String prot = "Tag: "+data+" "+"Data: "+in;
//					antwort.add(prot);
//				}
//			}
//			return antwort;
//		}
		
//		//Methode um ein Ger��t abzuschalten
//		public void TurnOffPower(){
//			Packet TurnOff = new Packet();
//			this.Start_Session(TurnOff);
//			byte[] cmnd = Util.shortTobyte(TurnOff.Cmnd_Turn_Off_Pwr);
//			byte[] packet = TurnOff.createPacket(TurnOff.Pid_Command_Data,cmnd);
//			send(pipeIn,packet,false);
//		}
		
		//Methode um WayPointss zu bekommen
//		public Vector getWayPoints(){
//			Vector<WayPoints> WayPointsList = new Vector<WayPoints>();
//			Packet WayPoints = new Packet();
//			this.Start_Session(WayPoints);
//			byte[] cmnd = Util.shortTobyte(WayPoints.Cmnd_Transfer_Wpt);
//			byte[] packet = WayPoints.createPacket(WayPoints.Pid_Command_Data,cmnd);
//			Vector antwort = send(pipeIn,packet,true);
//			
//			//System.out.println("WegPunkte");
//			
//			for (int x = 0; x < antwort.size()-1; x ++){
//				//Datentyp D110
//				byte[] antwortByte = (byte[]) antwort.elementAt(x);
//				WayPointsList.add(new WayPoints(antwortByte));
//			}
//			
//			return WayPointsList;
//		}
		
		//Um Transfers zu beenden
//		private void AbortTransmission(){
//			Packet Abort = new Packet();
//			this.Start_Session(Abort);
//			byte[] cmnd = Util.shortTobyte(Abort.Cmnd_Abort_Transfer);
//			byte[] packet = Abort.createPacket(Abort.Pid_Command_Data,cmnd);
//			send(pipeIn,packet,false);
//		}
		
		//Sendet einen WegPunkt an das Ger��t
		//Mehtode um eine Map upzuLoaden
//		private void sendWayPoints(byte[] b){
//			Packet WayPoints = new Packet();
//			this.Start_Session(WayPoints);
//			byte[] packet = WayPoints.createPacket(WayPoints.Pid_Records, new byte[0]);
//			
//			byte[] packet2 = WayPoints.createPacket(WayPoints.Pid_Wpt_Data, b);
//
//			byte[] packet3 = WayPoints.createPacket(WayPoints.Pid_Xfer_Cmplt, new byte[0]);
//			
//			//System.out.println("Send Pid_REcord");
//			//System.out.println("Debug: Pid");
//			//Util.showByteCode(packet);
//			send(pipeInterrupt,packet,true);
//			//System.out.println("Send Wpt");
//			//System.out.println("Debug Wpt: ");
//			send(pipeIn,packet2,false);
//			//System.out.println("Send Xfer");
//			//System.out.println("Debug Xfer: ");
//			//Util.showByteCode(packet3);
//			send(pipeInterrupt,packet3,true);
//
//		}
		
		//Methode um einen WegPunkt zu erstellen -> dieser wird dann upgeloadet
		//latitude & longitude muss als semicircle angegeben werden
//		public void createWayPoint(char[] id, double latitude, double longitude){
//			WayPoints newWayPoints = new WayPoints();
//			//Latitude & Longitude(wird in SemiCircles ��bernommen)
//			newWayPoints.setLatitude(latitude);
//			newWayPoints.setLongitude(longitude);
//			//ident
//			newWayPoints.setIdent(id);
//			//comment
//			char[] comment = new char[0];
//			newWayPoints.setComment(comment);
//			int anzahl = (74 + id.length + comment.length);
//			//generiert aus den bisherigen Daten ein BytePacket
//			sendWayPoints(newWayPoints.createBytePacket(anzahl));
//		}	
		
		//Methode um TrackRouten zu bekommen
//		public Vector getTrackRoute(){
//			System.out.println("Starte TrackListe");
//			Vector <TrackListe> RouteTrackListe = new Vector<TrackListe>();
//			Packet RouteTrack = new Packet();
//			this.Start_Session(RouteTrack);
//			byte[] cmnd = Util.shortTobyte(RouteTrack.Cmnd_Transfer_Trk);
//			byte[] packet = RouteTrack.createPacket(RouteTrack.Pid_Command_Data,cmnd);
//			//System.out.println(packet);
//			Vector antwort = send(pipeIn,packet,true);
//			int index = -1;
//			System.out.println("WegPunkte");
//			for (int x = 0; x < antwort.size(); x ++){
//				//Datentyp D110
//				byte[] antwortByte = (byte[]) antwort.elementAt(x);
//				Util.showByteCode(antwortByte);
//				if (antwortByte[4] == 99){
//					index ++;
//					RouteTrackListe.add(new TrackListe(antwortByte));
//				}else if (antwortByte[4] == 34){
//		//	System.out.println(index);
//					TrackListe tList = (TrackListe) RouteTrackListe.elementAt(index);
//					tList.add(new Track(antwortByte));
//				}
//			}
//			
//		/*	//Zum Debuggen
//			for (int x=0; x < RouteTrackListe.size(); x++){
//				TrackListe tList = (TrackListe) RouteTrackListe.elementAt(x);
//				tList.toString();
//					for (int y = 0; y < tList.getSize(); y++){
//					Track tr = (Track)tList.getTrack(y);
//					System.out.println(tr.toString());
//				}
//			}
//		*/
//			return RouteTrackListe;
//		}
}
