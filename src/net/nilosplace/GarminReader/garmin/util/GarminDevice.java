package net.nilosplace.GarminReader.garmin.util;
import javax.usb.*;

public class GarminDevice {
	
	public static short idVendor = 2334;
	public static short idProduct = 3;
	public static int bulkIn = 2;
	public static int bulkInterrupt = 1;
	public static int bulkOut = 0;

	private String name = "";
	private UsbDevice device = null;
	
	public GarminDevice(UsbDevice device) {
		System.out.println("Device: " + device);
		this.device = device;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UsbDevice getDevice() {
		return device;
	}
	public void setDevice(UsbDevice device) {
		this.device = device;
	}
	public String toString() {
		return device.toString();
	}
}
