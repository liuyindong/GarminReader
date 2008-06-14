package net.nilosplace.GarminReader;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

import dk.itu.haas.GPS.IDate;
import dk.itu.haas.GPS.IGPSlistener;
import dk.itu.haas.GPS.IPosition;
import dk.itu.haas.GPS.ITime;
import dk.itu.haas.GPS.IWaypoint;
import dk.itu.haas.GPS.IWaypointListener;
import dk.itu.haas.GPS.Garmin.GarminGPS;
import dk.itu.haas.GPS.Garmin.GarminListener;
import dk.itu.haas.GPS.Garmin.GarminPacket;

public class GarminReader implements IGPSlistener {
	
	private GregorianCalendar cal;
	private SimpleDateFormat format;
	
	public GarminReader() {
        CommPortIdentifier port;
        format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        cal = new GregorianCalendar(new Locale("en"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.ZONE_OFFSET, 0);
        cal.set(Calendar.DST_OFFSET, 0);

		try {
			port = CommPortIdentifier.getPortIdentifier("COM3");
			SerialPort port2 = (SerialPort)port.open("ComControl", 3000);
			GarminGPS gps = new GarminGPS(new BufferedInputStream(port2.getInputStream()), new BufferedOutputStream(port2.getOutputStream()));
			gps.addGPSlistener(this);
            gps.setAutoTransmit(true);
            gps.requestDate();
            gps.run();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
    	GarminReader reader = new GarminReader();
    }

	public void dateReceived(IDate arg0) {
		cal.set(Calendar.YEAR, arg0.getYear());
		cal.set(Calendar.MONTH, arg0.getMonth() - 1);
		cal.set(Calendar.DAY_OF_MONTH, arg0.getDay());
	}

	public void positionReceived(IPosition arg0) {
		System.out.print(this.format.format(cal.getTime()));
		System.out.println(" " + arg0.getLatitude() + " " + arg0.getLongitude());
		
	}

	public void timeReceived(ITime arg0) {
		cal.set(Calendar.HOUR_OF_DAY, arg0.getHours());
		cal.set(Calendar.MINUTE, arg0.getMinutes());
		cal.set(Calendar.SECOND, arg0.getSeconds());
	}

}
