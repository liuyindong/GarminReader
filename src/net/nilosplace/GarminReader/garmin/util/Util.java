package net.nilosplace.GarminReader.garmin.util;
import java.text.DecimalFormat;
public class Util {
	//Converts from short to Bytearray
	static public byte[] shortTobyte(short s) {
		short source = s;
		byte[] ergebnis = new byte[2];
		if (s > 0){
		
			ergebnis[0] = (byte) source;
			source >>>= 8;
			ergebnis[1] = (byte) source;
		}else{
			ergebnis[0] = (byte) source;
		}
		return ergebnis;
	}
	
	//Converts from int to Bytearray
	//Little Endian Form
	static public byte[] intTobyte (int i) {
		byte[] ergebnis = new byte[4];
		ergebnis[0] = (byte) (i & 0xff);
		ergebnis[1] = (byte) ((i & 0xff00) >> 8);
		ergebnis[2] = (byte) ((i & 0xff0000) >> 16);
		ergebnis[3] = (byte) ((i & 0xff000000) >> 24);
		return ergebnis;
	}
	
	//Converts from int to Bytearray
	//Big Endian Form
	static public byte[] intToByteArray (int i) {	
		byte[] ergebnis = new byte[4];
		ergebnis[3] = (byte) (i & 0xff);
		ergebnis[2] = (byte) ((i & 0xff00) >> 8);
		ergebnis[1] = (byte) ((i & 0xff0000) >> 16);
		ergebnis[0] = (byte) ((i & 0xff000000) >> 24);
		return ergebnis;
	}
	
	////Converts from int to char-array
	public static char[] intToChar ( int integer) {
		// temp array of char.
		char[] charArray = new char[4];  
			// do right shift to the bytes of the integer to reach the byte 
			// which we want then do ( and logic ) to delet 
			// the other values then save it in on byte.	
		charArray[0] = (char) (byte)((integer >> 24) & 0x000000FF);
		charArray[1] = (char) (byte)((integer >> 16) & 0x0000FF);
		charArray[2] = (char) (byte)((integer >> 8)  & 0x00FF);
		charArray[3] = (char) (byte)(integer &  0xFF  );	
		return charArray;
	}

	//Converts from ByteArray to Short
	static public short byteToShort(byte[] b,int start) {
		int i = ((b[start+1] & 0xff) << 8) + (b[start] & 0xff);
		return (short) i;
	}
	
	//Converts from ByteArray to int
	static public int byteToInt(byte[] b, int start) {
		int i = ((b[start + 3] & 0xff) << 24) + ((b[start + 2] & 0xff) << 16)
		+ ((b[start + 1] & 0xff) << 8) + (b[start] & 0xff);
		return i;
	}
	
	//Converts from Semicircles to Degrees	
	static public double semicirclesToDegree(double d) {
		double faktor = (180 / Math.pow(2, 31));
		return d * faktor;
	}
	//Converts from Degree to Semicircles
	static public double degreeToSemicircles(double d) {
		double faktor = (Math.pow(2, 31) / 180);
		return d * faktor;
	}
	
	//Helps to format Strings in a moderate way
	static public String formatString(double d) {
		DecimalFormat df = new DecimalFormat("#.000000");
		return df.format(d);
	}
	
	//Converts from ByteArray in float
	public static float byteToFloat (byte[] arr, int start) {
		int i = 0;
		int len = 4;
		int count = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[count] = arr[i];
			count++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}
	
	//Helps to print out the content of a ByteArray-packet 
	static public void showByteCode(byte[] b, String message) {
		System.out.println("*************************" + message + "*************************");
		for (int i = 0; i < b[8]+12; i++) {
			System.out.print("["+i+"]="+b[i] + " ");
		}
		System.out.println();
		System.out.println("*************************" + message + "*************************");
	}
	
	//Helps to print out the content of a ByteArray-packet
	//You can declare the lenght of the packet using the second parameter
	static public void showByteCode(byte[] b, int laenge){
		System.out.println("Debug-Information - ByteCode");
		System.out.println("****************************************************");
		for (int i = 0; i < laenge; i++){
			System.out.print("[ "+i+" ]: "+b[i]);
		}
		System.out.println();
		System.out.println("****************************************************");
	}
	
	
	//Searches for "null" in the string (to get the length of the string)
	static public int nullTerminator(byte[] b, int start){
		int x=0;
		int anzahl = b[8]+12;
		for (x=start; x < anzahl && b[x] != 0; x++);
		return x;
	}
	
	//helps printing the latitude 
	static public String LatHelp(double latitude){
		if (latitude < 0){
			return latitude+" �S";
		}else return latitude+"  �N";
	}
	
	static public double MeterToFeet(int meter){
		return 3.2808399*meter;
	}
	
	//helps printing the longitude
	static public String LongHelp(double longitude){
		if (longitude < 0){
			return longitude+"  �W";
		}else return longitude+" �E";
	}
}
