package net.nilosplace.GarminReader;

import static de.ailis.usb4java.jni.USB.usb_find_busses;
import static de.ailis.usb4java.jni.USB.usb_find_devices;
import static de.ailis.usb4java.jni.USB.usb_get_busses;
import static de.ailis.usb4java.jni.USB.usb_init;

import java.io.IOException;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

import de.ailis.usb4java.jni.USB_Bus;
import de.ailis.usb4java.jni.USB_Device;
import de.ailis.usb4java.jni.USB_Device_Descriptor;

public class GPSReader {

	public static void main(String[] args) {
		GPSReader gps = new GPSReader();
	}
	
	public GPSReader() {
		int garminVender = 2334;
		int garminProduct = 3;
		
		usb_init();
		usb_find_busses();
		usb_find_devices();
		
		

//		try {
//			HIDManager manager = HIDManager.getInstance();
//			HIDDeviceInfo[] infos = manager.listDevices();
//			System.out.println(infos);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		USB_Device dev = findDevice(garminVender, garminProduct);

		System.out.println(dev);
		
	}
	
	
	

	public USB_Device findDevice(int venderId, int productId) {
		USB_Bus bus = usb_get_busses();
		while (bus != null) {
			USB_Device device = bus.devices();
			while (device != null) {
				USB_Device_Descriptor desc = device.descriptor();
				//System.out.format("%04x:%04x%n", desc.idVendor(), desc.idProduct());
				//System.out.println(desc.idVendor() + " " + desc.idProduct());
				if(desc.idVendor() == venderId && productId == desc.idProduct()) {
					return device;
				}
				device = device.next();
			}
			bus = bus.next();
		}
		return null;
	}
	
}
