package net.nilosplace.GarminReader;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import dk.itu.haas.GPS.IDate;
import dk.itu.haas.GPS.IGPSlistener;
import dk.itu.haas.GPS.IPosition;
import dk.itu.haas.GPS.ITime;
import dk.itu.haas.GPS.Garmin.GarminGPS;

public class GarminReader implements IGPSlistener {
	
	private GregorianCalendar cal;
	private SimpleDateFormat format;

	
	private BufferedWriter bw;
	//private int lastSecond = -1;
	//private int currentSecond = 0;
	
	public GarminReader() {
        CommPortIdentifier port;
        format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        cal = new GregorianCalendar(new Locale("en"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.ZONE_OFFSET, 0);
        cal.set(Calendar.DST_OFFSET, 0);
 
		try {
			Calendar filecal = Calendar.getInstance();
			String filename = filecal.getTime().toString().replaceAll(" ", "_").replaceAll(":", "_") + ".txt";
			FileWriter fw = new FileWriter(filename);
	        bw = new BufferedWriter(fw);
			port = CommPortIdentifier.getPortIdentifier("COM3");
			SerialPort port2 = (SerialPort)port.open("ComControl", 2000);
            port2.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            port2.setDTR(true);
            port2.setRTS(true);
			GarminGPS gps = new GarminGPS(new BufferedInputStream(port2.getInputStream()), new BufferedOutputStream(port2.getOutputStream()));
			gps.addGPSlistener(this);
			//gps.addGarminListener(this);
            gps.setAutoTransmit(true);
            gps.requestDate();
            gps.requestPosition();
            gps.run();
		}catch (Exception e) {
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
		String out = this.format.format(cal.getTime());
		out += " " + arg0.getLatitude() + " " + arg0.getLongitude() + " " + (arg0.getAltitude() * 3.2808399);
		System.out.println(out);

		try {
			bw.write(out + "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void timeReceived(ITime arg0) {
		cal.set(Calendar.HOUR_OF_DAY, arg0.getHours());
		cal.set(Calendar.MINUTE, arg0.getMinutes());
		cal.set(Calendar.SECOND, arg0.getSeconds());
		//currentSecond = arg0.getSeconds();
	}

}
