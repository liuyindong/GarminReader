package dk.itu.haas.GPS;
import java.util.Vector;

/**
* This is the abstract base-class that encapsulates the functionality of a generic GPS-unit.
*/
public abstract class GPS {
	/** A vector containing references to the objects registered as listeners on this GPS. */
	protected Vector GPSlisteners;	

	/** A vector containing references to the objects registered as listeners for waypoint-data from the GPS.*/	
	protected Vector WaypointListeners;

	/** A vector containing references to the objects registered as listeners for transfers from the GPS.
	* A listener can't be directly add to this group, but will instead be added when it registers for some
	* other data that is being transmitted in transfer-groups.
	*/	
	protected Vector TransferListeners;
	
	protected GPS() {
		GPSlisteners = new Vector();
		WaypointListeners = new Vector();
		TransferListeners = new Vector();		
	}
	
	/** Adds the specified IGPSlistener to receive data from the GPS. */
	public void addGPSlistener(IGPSlistener l) {
		// Only allow a listener to be registered once.
		if (GPSlisteners.contains(l))
			return;
			
		GPSlisteners.add(l);
		return;
	}
	/** 
	* Adds l to the list of listeners interested in transfer-events.
	* Members of this list can't be directly added, but have to be added through addition of 
	* other listeners.
	*/
	protected void addTransferListener(ITransferListener l) {
		// Only allow a listener to be registered once.
		if (TransferListeners.contains(l))
			return;
			
		TransferListeners.add(l);
		return;
	}
	
	/** 
	* Adds l to the list of listeners interested in waypoint-data.
	* Also adds l to the list of transfer-listeners.
	*/	
	public void addWaypointListener(IWaypointListener l) {
		// Only allow a listener to be registered once.
		
		if (WaypointListeners.contains(l))
			return;
		
		addTransferListener(l);	
		
		WaypointListeners.add(l);
		return;				
	}
	
	/**
	* Removes the the Waypoint-listener l from the list of Waypoint-listeners.
	*/
	public void removeWaypointListener(IWaypointListener l) {
		while (WaypointListeners.removeElement(l)) {}
		return;
	}
	
	/**
	* Removes the the GPS-listener l from the list of GPS-listeners.
	*/
	public void removeGPSListener(IGPSlistener l) {
		while (GPSlisteners.removeElement(l)) {}
		return;
	}

	/**
	* Removes the the transfer-listener l from the list of transfer-listeners.
	*/
	protected void removeTransferListener(ITransferListener l) {
		while (TransferListeners.removeElement(l)) {}
		return;
	}	
			
	/**
	* Notifies listeners of the beginning of a stream of data. Tells listeners of the number of 
	* data-units in the transfer.
	*/
	public void fireTransferStart(int number) {
		for (int i = 0 ; i < TransferListeners.size() ; i++) {
			((ITransferListener) TransferListeners.elementAt(i)).transferStarted(number);
		}
	}
	
	/**
	* Goes through the list of Waypoint-listeners and distributes the waypoint wp.
	*/
	public void fireWaypointData(IWaypoint wp) {
		for (int i = 0 ; i < WaypointListeners.size() ; i++) {
			((IWaypointListener) WaypointListeners.elementAt(i)).waypointReceived(wp);
		}
	}
	
	/**
	* Notifies listeners of the end of a stream of data. 
	*/
	public void fireTransferComplete() {
		for (int i = 0 ; i < TransferListeners.size() ; i++) {
			((ITransferListener) TransferListeners.elementAt(i)).transferComplete();
		}
	}
	
	/**
	* Goes through the list of GPSlisteners and distributes the new position data.
	*/ 
	protected void firePositionData(IPosition pos) {
		for (int i = 0 ; i < GPSlisteners.size() ; i++) {
			((IGPSlistener) GPSlisteners.elementAt(i)).positionReceived(pos);
		}
	}

	/**
	* Goes through the list of GPSlisteners and distributes the new date data.
	*/ 	
	protected void fireDateData(IDate dat) {
		for (int i = 0 ; i < GPSlisteners.size() ; i++) {
			((IGPSlistener) GPSlisteners.elementAt(i)).dateReceived(dat);
		}	
	}
	
	/**
	* Goes through the list of GPSlisteners and distributes the new time data.
	*/ 
	protected void fireTimeData(ITime time) {
		for (int i = 0 ; i < GPSlisteners.size() ; i++) {
			((IGPSlistener) GPSlisteners.elementAt(i)).timeReceived(time);
		}	
	}

	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public abstract void requestPosition();
	
	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public abstract void requestTime();
	
	/** Makes a request for the specified data to the GPS. Data will be returned to all listeners through the IGPSlistener-interface. */
	public abstract void requestDate();

	/** 
	* Requests a descriptive string from the GPS. Should be formatted for human-reading. 	
	* The string should be constructed by every GPS-implementation upon startup.
	*/
	public abstract String getDescription();

	/** 
	* Asks the GPS to transmit all the waypoints in it's memory. The result will be returned through the WaypointListener-interface. 
	* Throws a FeatureNotSupportException if it isn't possible to do this on the GPS.
	*/
	public abstract void requestWaypoints();
	
	/** 
	* Asks the GPS to either start or stop transmitting data periodically. <br/>
	* The data will be the that which is accessible through the IGPSlistener-interface. 
	* Throws a FeatureNotSupportException if it isn't possible to do this on the GPS.
	*/
	public abstract void setAutoTransmit(boolean t);
	
	/** Stops communication with GPS.<br/>
	* Most likely, your program won't be able to shut down before you've called this method. 
	* If b is set to true, the GPS will be asked to turn off.	
	* If b is set to false, the GPS will remain turned on.
	* Throws a FeatureNotSupportException if b is true, but the GPS-unit doesn't support that function.
	*/
	public abstract void shutdown(boolean b);
}