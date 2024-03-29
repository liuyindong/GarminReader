package dk.itu.haas.GPS.test;
import javax.comm.*;
import java.util.Enumeration;
import java.io.*;

/** 
* This class is made to establish simple communication with the GPS. (Ev. 2)
*/

public class dumpInput {	

	public static void main(String args[]) {
		CommPortIdentifier portID;
		SerialPort port;
		if (args.length != 0) {
			System.out.println("Trying to acquire " + args[0]);			
			
			try {
				portID = CommPortIdentifier.getPortIdentifier(args[0]);
			} catch (NoSuchPortException e) {
				System.out.println(args[0] + " could not be opened. No such port.");
				return;
			}
			
			try {
				if (portID.getPortType() == portID.PORT_SERIAL) {
					port = (SerialPort) portID.open("dk.itu.haas.GPS", 3000);
					port.setSerialPortParams(9600, port.DATABITS_8, port.STOPBITS_1, port.PARITY_NONE);
					port.setFlowControlMode(port.FLOWCONTROL_NONE);
					System.out.println("Port configuration:");
					System.out.println("Baud-rate: " + port.getBaudRate());
					System.out.println("Parity: " + port.getParity());
					System.out.println("Data bits: " + port.getDataBits());
					System.out.println("Stop bits: " + port.getStopBits());
					System.out.println("Framing enabled: " + port.isReceiveFramingEnabled());
				} else {
					System.out.println("Parallel ports not yet supported.");
					return;
				}
			} catch (PortInUseException e) {
				System.out.println("Port is already in use by " + e.currentOwner);			
				return;
			} catch (UnsupportedCommOperationException e) {
				System.out.println("Problems configuring seriel port: " + e.getMessage());
				return;
			}
		} else {
			System.out.println("Specify port!");
			return;
		}
		
		BufferedInputStream input;
		BufferedOutputStream output;
		try {
			input = new BufferedInputStream(port.getInputStream());
			output = new BufferedOutputStream(port.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error getting serialport-streams!");
			return;
		}
		
		System.out.println("Got " + port.getName());

		// Attempt to get the GPS to shut down.
		System.out.println("Transmitting to GPS...");
		int[] turn_off = new int[8];
		turn_off[0] = 16;
		turn_off[1] = 10;
		turn_off[2] = 2;
		turn_off[3] = 8;
		turn_off[4] = 0;
		turn_off[5] = 236;
		turn_off[6] = 16;
		turn_off[7] = 3;
		try {
			for (int i = 0 ; i < 8 ; i++) {
				output.write(turn_off[i]);
			}
			output.flush();
		} catch (IOException e) {
			System.out.println("Error writing to GPS! Bailing out!");
			System.out.println(e.getMessage());
			return;
		}

		System.out.println("Sleeping for 3 secs...");
		try {
			Thread.sleep(3000);			
		} catch (InterruptedException e) {
			
		}

		try {			
			int read;
			while (input.available() > 0) {
				read = input.read();
				if (read == 16)
					System.out.print('\n');
				System.out.print( toHex(read) + " ");

			}
		} catch (IOException e) {
			System.out.println("IOException! " + e.getMessage());
			return;
		}
	}
	
	public static String toHex(int a) {
		if (a < 16)
			return "0" + Integer.toHexString(a);
		else
			return Integer.toHexString(a);
	}
}