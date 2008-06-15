package net.nilosplace.GarminReader;

import java.io.IOException;
import java.io.InputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

public class RawReader {
	public RawReader() {
		CommPortIdentifier port;
		try {
			port = CommPortIdentifier.getPortIdentifier("COM3");
			SerialPort port2 = (SerialPort)port.open("ComControl", 2000);
			InputStream in = port2.getInputStream();
			int counter = 0;
			while(true || counter < 100000) {
				System.out.println((char)in.read());
				counter++;
			}
			port2.close();
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
    public static void main(String[] args) {
    	RawReader reader = new RawReader();
    }
}
