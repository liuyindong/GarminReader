package net.nilosplace.GarminReader.garmin;



public class GPSReader {

	public static void main(String[] args) {
		GPSReader gps = new GPSReader();
		gps.run();
	}
	
	public GPSReader() {
		
	}
	
	public void run() {

		
		try {
			
			GarminApi api = new GarminApi();
			
			
			byte[] packet = api.startSession();
			byte[] packet2 = api.getAllProductProtocolData();
			
			//api.startPVTData();
			
			//Util.showByteCode(packet);
		
			
	
			//Util.showByteCode(packet2);
			
			
			
			//api.startPVTData();
			//System.out.println(api.);
			
			
			//System.out.println(api.getProductData());

			//Thread.sleep(5000);
			api.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
