package net.nilosplace.GarminReader.garmin;

public class LogicHandler {
	private GarminApi api;
	private GPSReader gui;
	private boolean run = true;

	public LogicHandler() {
		//run = false;
	}
	
	public void getProductInfo() {
		if(run) api.getAllProductProtocolData();
	}

	public void startData() {
		if(run) api.startPVTData();
	}

	public void stopData() {
		if(run) api.stopPVTData();
	}

	public void setGUI(GPSReader gpsReader) {
		if(run) gui = gpsReader;
	}

	public void close() {
		if(run) api.close();
	}

	public void init() {
		if(run) api = new GarminApi();
		if(run) api.startSession();
	}
	
	public void getPacket() {
		api.getPacket();
	}
	
	public void setProductInfo(String info) {
		gui.setProductInfo(info);
	}
	
	public void setCurrnetLocation(byte[] location) {
		gui.setCurrnetLocation(location);
	}
}
