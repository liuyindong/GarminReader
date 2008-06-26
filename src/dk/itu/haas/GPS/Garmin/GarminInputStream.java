package dk.itu.haas.GPS.Garmin;
import java.io.*;

/**
* This class provides the functionality of automatically removing the double DLEs from the GPS-inputstream.
* The double-DLEs can be found in the size-,data-, and checksum-fields. 
* The only method providing the filtering-functionality is read().
*/
public class GarminInputStream extends FilterInputStream {
	/*
	* Last value read.
	*/
	private int prev;
	//private boolean error = false;
	//private boolean midStreamBoundry = false;
	//BufferedInputStream bin;
	
	/**
	* Takes the stream to the GPS-unit as an argument.
	*/
	public GarminInputStream(InputStream i) {
		super(i);
		in = i;
		prev = 0;
	}		
			
	/**
	* Reads a packet from the stream. <br/>
	* <b> Note: </b> Method assumes that it's called between packets, ie. when the first byte of a packet is the
	* next in the stream. If this condition is met, the method will leave the stream in the same state.
	*/	
	public int[] readPacket() throws InvalidPacketException, IOException {
		int c = 0;
		int[] packet;
		int[] storage = new int[265];
		int size = 0;
		
		while(c != GarminPacket.DLE) {
			c = read();
			storage[size] = c;
		}
		while(true) {
			c = read();
			if(c == GarminPacket.ETX && storage[size] == GarminPacket.DLE) {
				size++;
				storage[size] = c;
				break;
			}			
			size++;
			storage[size] = c;
		}
		size++;
		
		packet = new int[size];
		for(int i = 0; i < size; i++) {
			packet[i] = storage[i];
		}
		if(packet.length < 3) {
			throw (new InvalidPacketException(packet, 0));
		}
		return packet;
	}
	
	/**
	* Returns the next byte from the stream. Makes sure to remove DLE stuffing.	
	*/	
	public int read() throws IOException{
		int c = in.read();
		if ( prev == 16 && c == 16) {
			return prev = in.read();
		} else {
			return prev = c;
		}
	}
}