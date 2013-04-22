package net.nilosplace.GarminReader.garmin;


public class GPSReader {

	public static void main(String[] args) {
		GPSReader gps = new GPSReader();
	}
	
	public GPSReader() {
		//int garminVender = 2334;
		//int garminProduct = 3;
		
//		usb_init();
//		usb_find_busses();
//		usb_find_devices();
		
	try {
		
		GarminApi api = new GarminApi();
		
		//api.startSession();
		
		//api.getAllProductProtocolData();
		//System.out.println(api.);
		
		
		//System.out.println(api.getProductData());
	
		

//			UsbServices services = UsbHostManager.getUsbServices();
//			UsbHub rootHub = services.getRootUsbHub();
//			UsbDevice device = findDevice(rootHub, garminVender, garminProduct);
//			
//			device.addUsbDeviceListener(new UsbDeviceListener() {
//				public void usbDeviceDetached(UsbDeviceEvent arg0) {
//					System.out.println(arg0);
//				}
//				public void errorEventOccurred(UsbDeviceErrorEvent arg0) {
//					System.out.println(arg0);
//				}
//				public void dataEventOccurred(UsbDeviceDataEvent arg0) {
//					System.out.println(arg0);
//				}
//			});
//		
//			List<Usb4JavaConfiguration> list = device.getUsbConfigurations();
//			
//			
//			
//			for(Usb4JavaConfiguration config: list) {
//				System.out.println(config);
//				
//				for(UsbInterface i: config.getUsbInterfaces()) {
//					System.out.println(i.isClaimed());
//					
////					UsbInterfacePolicy p = new UsbInterfacePolicy() {
////						public boolean forceClaim(UsbInterface arg0) {
////							return false;
////						}
////					};
//					
//					i.claim();
//					i.getUsbEndpoints();
//					List<Usb4JavaEndpoint> list2 = i.getUsbEndpoints();
//					for(Usb4JavaEndpoint t: list2) {
//						Usb4JavaPipe pipe = t.getUsbPipe();
//						pipe.addUsbPipeListener(new UsbPipeListener() {
//							public void errorEventOccurred(UsbPipeErrorEvent arg0) {
//								System.out.println(arg0);
//							}
//							public void dataEventOccurred(UsbPipeDataEvent arg0) {
//								System.out.println(arg0);
//							}
//						});
//						UsbIrp irp = pipe.createUsbIrp();
//						System.out.println(irp.getData());
//								
//						pipe.open();
//						System.out.println(pipe.isOpen());
//						//System.out.println(pipe.createUsbIrp());
//						
//					}
//				}
//			}
//			
			Thread.sleep(50000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private UsbDevice findDevice(UsbDevice device, int venderId, int productId) {
//		UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
//		//System.out.format("%04x:%04x%n", desc.idVendor() & 0xffff, desc.idProduct() & 0xffff);
//		if (device.isUsbHub()) {
//			UsbHub hub = (UsbHub) device;
//			for (UsbDevice child : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
//				UsbDevice dev = findDevice(child, venderId, productId);
//				if(dev != null) {
//					return dev;
//				}
//			}
//		}
//		
//		if(desc.idProduct() == productId && desc.idVendor() == venderId) {
//			return device;
//		}
//		return null;
//	}
	
//	public USB_Device findDevice(int venderId, int productId) {
//		USB_Bus bus = usb_get_busses();
//		while (bus != null) {
//			USB_Device device = bus.devices();
//			while (device != null) {
//				USB_Device_Descriptor desc = device.descriptor();
//				//System.out.format("%04x:%04x%n", desc.idVendor(), desc.idProduct());
//				//System.out.println(desc.idVendor() + " " + desc.idProduct());
//				if(desc.idVendor() == venderId && productId == desc.idProduct()) {
//					return device;
//				}
//				device = device.next();
//			}
//			bus = bus.next();
//		}
//		return null;
//	}
}
