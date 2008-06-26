package net.nilosplace.GarminReader;

import java.io.IOException;
import java.io.InputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import dk.itu.haas.GPS.Garmin.GarminInputStream;
import dk.itu.haas.GPS.Garmin.GarminPacket;
import dk.itu.haas.GPS.Garmin.InvalidPacketException;

public class RawReader {
	public RawReader() {
		int errorcount = 0;
		CommPortIdentifier port;
		try {
			port = CommPortIdentifier.getPortIdentifier("COM6");
			SerialPort port2 = (SerialPort)port.open("ComControl", 3000);
            port2.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            port2.setDTR(true);
            port2.setRTS(true);
			InputStream in = port2.getInputStream();
			GarminInputStream gis = new GarminInputStream(in);
			GarminPacket pack = null;
			int counter = 0;
			System.out.println("We are going into the while loop");
			while(counter < 100000) {
				try {
					if (gis.available() == 0) {
						try {
							Thread.sleep(500);
						} catch(InterruptedException e) {}
						continue;
					}
					//System.out.println("Data Ready Reading packet");
					pack = new GarminPacket(gis.readPacket(), true);
					errorcount = 0;
					System.out.println("Packet: " + pack.getRawPacket());
					counter++;
				} catch (InvalidPacketException e) {
					errorcount++;
					if(errorcount > 256) {
						System.out.println("Too much bad data: ");
						e.printStackTrace();
						errorcount = 0;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			port2.close();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}
    public static void main(String[] args) {
    	RawReader reader = new RawReader();
    }
}