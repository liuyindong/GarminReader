package dk.itu.haas.GPS.Garmin;
import dk.itu.haas.GPS.*;
/**
* A class that encapsulates the basic functionality of a packet.
*/
public class GarminPacket {
	// L000 values. (Names taken from protocol specification.)
	public static final int Pid_Ack_Byte = 6;
	public static final int Pid_Nak_Byte = 21;
	public static final int Pid_Protocol_Array = 253;
	public static final int Pid_Product_Rqst = 254;
	public static final int Pid_Product_Data = 255;
	
	// L001 values.
	public static final int Pid_Command_Data = 10;
	public static final int Pid_Xfer_Cmplt = 12;
	public static final int Pid_Date_Time_Data = 14;	
	public static final int Pid_Position_Data = 17;
	public static final int Pid_Records = 27;
	public static final int Pid_Wpt_Data = 35;
	public static final int Pid_Pvt_Data = 51;
	
	
	// A010 - Device Command Protocol.
	/** Abort current transfer. */
	public static final int Cmnd_Abort_Transfer = 0;
	/** Transfer almanac. */
	public static final int Cmnd_Transfer_Alm 	= 1;	
	/** Transfer position. */
	public static final int Cmnd_Transfer_Posn 	= 2;
	/** Transfer proximity waypoints. */
	public static final int Cmnd_Transfer_Prx 	= 3;
	/** Transfer routes. */
	public static final int Cmnd_Transfer_Rte	= 4;
	/** Transfer time. */
	public static final int Cmnd_Transfer_Time 	= 5;	
	/** Transfer track log. */
	public static final int Cmnd_Transfer_Trk	= 6;
	/** Transfer waypoints. */
	public static final int Cmnd_Transfer_Wpt	= 7;
	/** Turn off power. */
	public static final int Cmnd_Turn_Off_Pwr 	= 8;	
	/** Start transmitting PVT (Position, velocity, time) Data. */
	public static final int Cmnd_Start_Pvt_Data = 49;
	/** Stop transmitting PVT (Position, velocity, time) Data. */
	public static final int Cmnd_Stop_Pvt_Data	= 50;
	
	// Packet Boundaries.
	/**
	* Data link escape. Packet boundary.
	*/
	public static final int DLE = 16; 
	/**
	* End of text. Packet boundary.
	*/
	public static final int ETX = 3; 
	
	/**
	* The packet in byte-form.
	* It is required that the array-length is trimmed to the size of the packet.
	*/
	protected int[] packet;

	/**
	* Creates a new GarminPacket with the contents of p. 
	* Throws InvalidPacketException if packet is malformed.
	*/
	public GarminPacket(int[] p){
		this(p, false);
	}
	
	/**
	* Creates a new GarminPacket with the contents of p. 
	* if calcChecksum is true, the packet will have it's checksum recalculated.
	* Throws InvalidPacketException if packet is malformed.
	*/
	
	public GarminPacket(int[] p, boolean calcChecksum) {
		packet = (int[]) p.clone();

		if (calcChecksum) {
			packet[packet.length - 3] = calcChecksum();
		}
		
		if (isLegal() != -1) {
			System.out.println("Error in byte: " + isLegal());
			throw (new InvalidPacketException(p, isLegal()));
		}
	}
	
	/**
	* Calculates the checksum for the packet.
	* Does <b> not </b> insert it into the correct position of the int[] packet array. <br/>
	* The method assumes that the packet is a valid Garmin-packet with all values containing their final
	* values.
	*/	
	public int calcChecksum() {
		int sum = 0;
		for (int i = 1 ; i <= packet.length - 4 ; i++) {
			sum += packet[i];		
		}		
		
		sum = sum % 256;
		sum = sum ^ 255;						
		sum += 1;
		return sum;	
	}
	
	/**
	* Returns the ID (ie. type) of the packet.
	*/ 
	public int getID() {
		return packet[1];
	}
	
	/**
	* Returns the amount of bytes in the data-field of this packet.	
	*/ 
	public int getDataLength() {
		return packet[2];
	}
	
	/**
	* Returns the packet-byte at position i.
	*/	
	protected int getByte(int i) {
		return packet[i];
	}
	
	/**
	* Returns the packet in it's original byte-form. 
	* <br/><b> Note:</b> The array returned is a clone of the array contained in the class. 
	* Changing the values in the array will not affect the contents of the class.
	*/	
	protected int[] getPacket() {
		return (int[]) packet.clone();
	}
	
	/**
	* Returns the length of the entire packet in bytes.
	*/	
	protected int getLength() {
		return packet.length;
	}
	
	/**
	* Method that reads a Garmin-word in the packet and returns it as an int.
	* This method can be used to read both int and word from a Garmin-packet.
	*/	
	protected int readWord(int packet_index) {
		int sum = packet[packet_index++];
		sum += packet[packet_index++] << 8;
		return sum;
	}

	/**  
	* Method that reads a Garmin-long in the packet and returns it as an int.
	*/ 	
	protected int readLong(int packet_index) {
		int res = packet[packet_index++];
		res += packet[packet_index++] << 8;
		res += packet[packet_index++] << 16;
		res += packet[packet_index++] << 24;
		
		return res;
	}

	/**
	* Method that reads a null-terminated string.
	*/
	protected String readNullTerminatedString(int packet_index) {
		StringBuffer res = new StringBuffer(20);
		while ((packet[packet_index] != 0) && (packet_index != packet.length )) {
			res.append( (char) packet[packet_index++]);			
			
		}
		return res.toString();
	}
	
	/**
	* Method that translates a packet-id into a human-readable string. 
	*/	
	public static String idToString(int id) {
		switch (id) {
			case Pid_Ack_Byte :	
				return "Acknowledge packet";
			case Pid_Command_Data :
				return "Command packet";
			case Pid_Date_Time_Data :
				return "Date and time data";
			case Pid_Nak_Byte :
				return "Not acknowledged packet";
			case Pid_Product_Data :
				return "Product data.";
			case Pid_Product_Rqst :
				return "Product request";
			case Pid_Protocol_Array :
				return "Protocol array packet";
			case Pid_Position_Data :
				return "position data";
			case Pid_Pvt_Data :
				return "PVT data";
			case Pid_Records :
				return "Start of record transfer";
			case Pid_Wpt_Data :
				return "waypoint data";
			default :
				return "unknown data";
		}
	}
	
	/**
	* <i> Debug-method. </i>
	* Returns a String-representation of the bytes in the packet. 
	*/	
	public String getRawPacket() {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < packet.length ; i++)
			s.append(" " + packet[i]);
		return s.toString();
	}
	
	/**
	* This is a factory-method capable of creating instances the commandpackets from A010. (Device Command Protocol 1)
	* returns null if it can't make a packet from the argument supplied.	
	* <br/> <i>type<i/> can be one of the following constants:
	* <ul>
	* <li> Cmnd_Turn_Off_Pwr 
	* <li> Cmnd_Transfer_Posn
	* <li> Cmnd_Transfer_Time
	* <li> Cmnd_Abort_Transfer
	* <li> Cmnd_Transfer_Alm 	
	* <li> Cmnd_Transfer_Prx 
	* <li> Cmnd_Transfer_Rte 
	* <li> Cmnd_Transfer_Trk 
	* <li> Cmnd_Transfer_Wpt 
	* <li> Cmnd_Start_Pvt_Data 
	* <li> Cmnd_Stop_Pvt_Data
	* </ul>
	*/
	public static GarminPacket createCommandPacket(int type) {
		switch (type) {
			case Cmnd_Turn_Off_Pwr :
			case Cmnd_Transfer_Posn:
			case Cmnd_Transfer_Time:
			case Cmnd_Abort_Transfer :
			case Cmnd_Transfer_Alm :
			case Cmnd_Transfer_Prx :
			case Cmnd_Transfer_Rte :
			case Cmnd_Transfer_Trk :
			case Cmnd_Transfer_Wpt :
			case Cmnd_Start_Pvt_Data :
			case Cmnd_Stop_Pvt_Data	:
				return new GarminPacket(new int[] {DLE, Pid_Command_Data, 2, type,0 , 0, DLE, ETX}, true);
			default :			
				return null;
		}
	}

	/**
	* This method is capable of making the data-packets from L000 (basic link protocol).
	* <br/> <i>type</i> can be one of the following constants:
	* <ul>
	* <li> Pid_Ack_Byte 
	* <li> Pid_Nak_Byte 
	* <li> Pid_Protocol_Array
	* <li> Pid_Product_Rqst
	* <li> Pid_Product_Data
	* </ul>
	* The argument <i> data </i> is an array of int that will be put in the data-field of the packet.
	*/	
	public static GarminPacket createBasicPacket(int type, int[] data) {
		switch (type) {
			case Pid_Ack_Byte : 
			case Pid_Nak_Byte :
			case Pid_Protocol_Array :
			case Pid_Product_Rqst:
			case Pid_Product_Data :
				int[] packet = new int[data.length + 6];
				packet[0] = DLE; packet[1] = type;
				packet[2] = data.length;
				System.arraycopy(data, 0, packet, 3, data.length);
				packet[packet.length - 3] = 0;
				packet[packet.length - 2] = DLE;
				packet[packet.length - 1] = ETX;
				return new GarminPacket(packet, true);
			default : 
				return null;
		}
	}
	
	/**
	* Checks if the packet is valid with regards to header, footer,data-field-length and checksum.
	* Returns the index of the illegal byte. If packet is ok, -1 is returned.
	*/
	public int isLegal() {
		if (packet[0] != DLE)
			return 0;
			
		int size = packet[2];
		
		if (size + 6 != packet.length)
			return 2;
			
		if ( packet[packet.length  - 3] != calcChecksum() )
			return packet.length  - 3;
			
		if ( packet[packet.length  - 2] != DLE )
			return packet.length  - 2;
		
		if ( packet[packet.length  - 1] != ETX )
			return packet.length  - 1;
			
		return -1;
	}
	
	/**
	* Method that reads a Garmin-byte in the packet and returns it as a short.
	*/	
	protected short readByte(int packet_index) {
		return (short) packet[packet_index];
	}
	
	/**
	* Method that reads a Garmin-double in the packet and returns it as a double.
	*/
	protected double readDouble(int packet_index) {								
		long res = 0; 		
		
		res += ( (long) packet[packet_index++] );
		res += ( (long) packet[packet_index++] ) << 8;
		res += ( (long) packet[packet_index++] ) << 16;
		res += ( (long) packet[packet_index++] ) << 24;
		res += ( (long) packet[packet_index++] ) << 32;
		res += ( (long) packet[packet_index++] ) << 40;
		res += ( (long) packet[packet_index++] ) << 48;
		res += ( (long) packet[packet_index++] ) << 56;		
				
		return Double.longBitsToDouble(res);
	} 

	/**
	* Returns a human-readable string with information to the packet's contents.
	*/ 

	public String toString() {
		return "GarminPacket containing " + idToString(getID());
	}

	/**
	* Method that reads a Garmin-float in the packet and returns it as a float.
	*/	
	protected float readFloat(int packet_index) {
		int res = 0;
		res += packet[packet_index++];
		res += packet[packet_index++] << 8;
		res += packet[packet_index++] << 16;
		res += packet[packet_index++] << 24;

		return Float.intBitsToFloat(res);		
	}

	

}