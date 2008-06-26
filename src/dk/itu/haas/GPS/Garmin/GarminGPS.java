package dk.itu.haas.GPS.Garmin;
import dk.itu.haas.GPS.*;
import java.io.*;
import java.util.Vector;

public class GarminGPS extends GPS implements Runnable {
	GarminInputStream input;
	GarminOutputStream output;
	
	/** A human-readable description of the GPS-unit. */ 
	protected String description;
	
	Thread listener;
	/** The listening thread will be active as long as this variable remains true. */
	protected boolean active; 
	/** A vector containing references to all the GarminListeners. */
	protected Vector GarminListeners;
	
	/** */ 
	public GarminGPS(BufferedInputStream i, BufferedOutputStream o) {
		input = new GarminInputStream(i);
		output = new GarminOutputStream(o);
		listener = new Thread(this);
		listener.start();
		active = true;
		GarminListeners = new Vector();
		
		// Request product information. 
		try {
			output.write(GarminPacket.createBasicPacket(GarminPacket.Pid_Product_Rqst, new int[] {}));
		} catch(IOException e) {}
	}
	
	
	/**
	* Adds the specified GarminListener to receive all packets sent from the GPS. 	
	*/
	public void addGarminListener(GarminListener l) {
		// Only allow a listener to be registered once.
		if (GarminListeners.contains(l))
			return;
			
		GarminListeners.add(l);
		return;
	}

	/**
	* Removes the specified GarminListener from the list of listeners.
	*/ 
	public void removeGarminListener(GarminListener l) {
		while (GarminListeners.removeElement(l)) {}
		return;
	}

	/** 
	* Goes through the list of GarminListeners and transmits p to them.
	*/	
	protected void fireGarminPacket(GarminPacket p) {
		for (int i = 0 ; i < GarminListeners.size() ; i++) {
			((GarminListener) GarminListeners.elementAt(i)).GarminPacketReceived(p);
		}	
	}
	
	/** This method is listening for input from the GPS. */ 
	public void run() {
		GarminPacket pack = null;		
		int id = 0;
		int errorcount = 0;
		
		while (active) {
			try {
				if (input.available() == 0) {
					try {
						Thread.sleep(500);
					} catch(InterruptedException e) {}
					continue;
				}
				pack = new GarminPacket(input.readPacket(), true);
				id = pack.getID();
				errorcount = 0;
				//System.out.println("Do we get to this code?");
				output.write( GarminPacket.createBasicPacket(GarminPacket.Pid_Ack_Byte, new int[] {id, 0}));
				fireGarminPacket(pack);
				Distribute(pack);
			} catch (IOException e) {
				active = false;				
				return;
			} catch (InvalidPacketException e) {
				errorcount++;
				if(errorcount > 256) {
					System.out.println("Too much bad data: ");
					//System.out.println(pack.getRawPacket());
					e.printStackTrace();
					errorcount = 0;
				}
			}	
		}
	}
	
	/** This method is used to identify the type of packet received, and distribute it to the correct 
	* listeners. 
	*/
	protected void Distribute(GarminPacket p) {		
		switch (p.getID()) {
			case GarminPacket.Pid_Position_Data :
				firePositionData(new PositionDataPacket(p));
				return;
			case GarminPacket.Pid_Date_Time_Data :
				TimeDataPacket tdp = new TimeDataPacket(p);
				fireDateData(tdp);
				fireTimeData(tdp);
				return;
			case GarminPacket.Pid_Pvt_Data :
				PVTDataPacket pvtp = new PVTDataPacket(p);		
				fireTimeData(pvtp);
				firePositionData(pvtp);
				return;				
			case GarminPacket.Pid_Records :
				fireTransferStart((new RecordsPacket(p)).getNumber());
				return;
			case GarminPacket.Pid_Wpt_Data : 
				fireWaypointData( new WaypointDataPacket(p));
				return;
			case GarminPacket.Pid_Xfer_Cmplt :
				fireTransferComplete();
				return;
			case GarminPacket.Pid_Product_Data :
				//System.out.println("Product data arrived!");
				ProductDataPacket pp = new ProductDataPacket(p);
				description = pp.getDescription();
				description += "\nSoftware version: " + pp.getSWVersion();
				description += "\nProduct ID: " + pp.getProductID();
				return;
			case GarminPacket.Pid_Protocol_Array :
				description += "\nProtocols supported:\n";
				description += (new ProtocolDataPacket(p)).toString();
				return;
			default :
				return;
		}
	}

	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public void requestPosition() {
		try {
			output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Transfer_Posn));
		} catch (IOException e) {}
	}
	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public void requestTime() {
		try {
			output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Transfer_Time));
		} catch (IOException e) {}		
	}
	
	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public void requestDate() {
		try {
			output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Transfer_Time));
		} catch (IOException e) {}		
	}

	/** 
	* Asks the GPS to transmit all the waypoints in it's memory. The result will be returned through the WaypointListener-interface. 
	*/
	public void requestWaypoints() {
		try {
			output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Transfer_Wpt));
		} catch (IOException e) {}
	}
			
	/** 
	* Asks the GPS to either start or stop transmitting data periodically. <br/>
	* The data will be that which is accessible through the IGPSlistener-interface. 
	* Throws a FeatureNotSupportException if it isn't possible to do this on the GPS.
	*/
	public void setAutoTransmit(boolean t) {
		if (t) {
			try {
			output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Start_Pvt_Data));
			} catch (IOException e) {}		
		} else {
			try {
				output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Stop_Pvt_Data));
			} catch (IOException e) {}					
		}
	}
	
	/** Stops communication with GPS.<br/>
	* Most likely, your program won't be able to shut down before you've called this method. 
	* If b is set to true, the GPS will be asked to turn off.	
	* If b is set to false, the GPS will remain turned on.
	*/
	public void shutdown(boolean b) {
		if (b) { 
			try {
				output.write( GarminPacket.createCommandPacket(GarminPacket.Cmnd_Turn_Off_Pwr));
			} catch (IOException e) {}
		}
		active = false;		
	}
	
	/** 
	* Returns a string telling the brand of the Garmin-gps, software version and the protocols supported.
	*/ 
	public String getDescription() {
		return description;
	}
}