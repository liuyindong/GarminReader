package dk.itu.haas.GPS.test;

public class test {
	public static void main(String args[]) {
		byte[] out = shortTobyte((short)1010);
		System.out.println("Short Max-value: " + out[0] + " " + out[1]);
	}
	
	public static byte[] shortTobyte(short s) {
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
}