package net.nilosplace.GarminReader.garmin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;
import javax.usb.UsbNotOpenException;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;

import net.nilosplace.GarminReader.garmin.listeners.InListener;
import net.nilosplace.GarminReader.garmin.listeners.InterListener;
import net.nilosplace.GarminReader.garmin.listeners.OutListener;

public class GarminUSBController extends Thread {

	protected UsbIrp irpInSend;
	protected UsbIrp irpOutSend;
	protected UsbIrp irpInterSend;

	protected Semaphore recvsem = new Semaphore(1);
	protected Semaphore sendsem = new Semaphore(1);
	
	protected int maxBufferSize = 64;
	protected int maxPacketSize = 1024;
	protected byte[] returnedPacket;
	private GarminDevice garminDevice = null;
	
	protected UsbPipe pipeIn;
	protected UsbPipe pipeOut;
	protected UsbPipe pipeInter;
	
	protected LinkedList<byte[]> send = new LinkedList<byte[]>();
	protected LinkedList<byte[]> recv = new LinkedList<byte[]>();
	
	protected boolean initialized = false;
	
	public GarminUSBController() throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException {

		try {
			UsbServices services = UsbHostManager.getUsbServices();
			
			UsbHub roothub = services.getRootUsbHub();
			
			garminDevice = findDevices(roothub, 0);
			
			if(garminDevice != null) {
				System.out.println("Device found: " + garminDevice);
				initDevice(garminDevice);
			} else {
				System.out.println("Error Garmin Device Could not be found");
				System.exit(0);
			}
			
	
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public GarminDevice findDevices(UsbHub hub, int level){
		List<UsbDevice> devices = hub.getAttachedUsbDevices();
		//System.out.println(hub);
		for(UsbDevice device: devices) {
			if (device.isUsbHub()) {
				GarminDevice dev = findDevices((UsbHub) device, level + 1);
				if(dev != null) {
					return dev;
				}
			} else{
				//System.out.println(device);
				UsbDeviceDescriptor descriptor = null;
				try{
					descriptor = device.getUsbDeviceDescriptor();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				if (descriptor.idProduct() == GarminDevice.idProduct && descriptor.idVendor() == GarminDevice.idVendor) {
					return new GarminDevice(device);
				}
			}
		}
		return null;
	}
	
	public void initDevice(GarminDevice device) throws UsbNotActiveException, UsbNotOpenException, UsbDisconnectedException, UsbException{
		UsbEndpoint pipeInEndP = null;
		UsbEndpoint pipeOutEndP = null;
		UsbEndpoint pipeInterruptP = null;
		UsbInterface interf = null;
		
		try {
			device.getDevice().getActiveUsbConfiguration();
			UsbConfiguration config = device.getDevice().getActiveUsbConfiguration();

			interf = config.getUsbInterface((byte)0);
			interf.claim(new UsbInterfacePolicy() {
				public boolean forceClaim(UsbInterface usbInterface) {
					return true;
				}
			});

			List<UsbEndpoint> totalEndpoints = interf.getUsbEndpoints();
			
			pipeInEndP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkIn);
			pipeOutEndP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkOut);
			pipeInterruptP = (UsbEndpoint) totalEndpoints.get(GarminDevice.bulkInterrupt);
			
			pipeIn = pipeInEndP.getUsbPipe();
			pipeIn.addUsbPipeListener(new InListener(this));
			
			pipeOut = pipeOutEndP.getUsbPipe();
			pipeOut.addUsbPipeListener(new OutListener(this));

			pipeInter = pipeInterruptP.getUsbPipe();
			pipeInter.addUsbPipeListener(new InterListener(this));

		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		initialized = true;
	}
	
	public void send(Packet packet) {
		try {
			sendsem.acquire();
			send.push(packet.createPacket());
			sendsem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void sendToUsb(byte[] packet) {
		try {
			System.out.println("Sending to pipeOut");
			returnedPacket = null;
			if (!pipeOut.isOpen()){
				pipeOut.open();
			}
			if(irpOutSend == null) {
				irpOutSend = pipeOut.createUsbIrp();
			}

			irpOutSend.setComplete(false);
			irpOutSend.setData(packet);
			irpOutSend.setActualLength(packet.length);
			Util.showByteCode(packet, "sendToUsb: pipeOut");
			pipeOut.syncSubmit(irpOutSend);
			if(irpOutSend.isUsbException()) {
				System.out.println("Exception:");
			}
		} catch (Exception e) {
			System.out.println("Sending Exception");
			e.printStackTrace();
			close();
			System.exit(0);
		}
	}
	
	public byte[] receiveFromUsb() {
		byte[] packet = new byte[maxBufferSize];
		try {
			System.out.println("Sending to pipeInter");
			if (!pipeInter.isOpen()){
				pipeInter.open();
			}
			if(irpInterSend == null) {
				irpInterSend = pipeInter.createUsbIrp();
			}


			irpInterSend.setAcceptShortPacket(true);
			irpInterSend.setComplete(false);
			irpInterSend.setData(packet);
			irpInterSend.setActualLength(packet.length);
			//Util.showByteCode(packet, "receiveFromUsb");
			pipeInter.asyncSubmit(irpInterSend);

			if(irpInterSend.isUsbException()) {
				System.out.println("Inter USB Exception:");
				System.out.println("Error: " + irpInterSend.getUsbException().getMessage());
				pipeInter.close();
			}
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		UsbInterface inter = pipeOut.getUsbEndpoint().getUsbInterface();
		try {
			if(pipeOut.isOpen()) {
				pipeOut.abortAllSubmissions();
				pipeOut.close();
			}
			if(pipeIn.isOpen()) {
				pipeIn.abortAllSubmissions();
				pipeIn.close();
			}
			if(pipeInter.isOpen()) {
				pipeInter.abortAllSubmissions();
				pipeInter.close();
			}
			inter.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void errorEventOccurred(UsbPipeErrorEvent arg0) {
		System.out.println("Error: " + arg0.toString());
		System.out.println("Error: " + arg0.getUsbException().getMessage());
		try {
			arg0.getUsbPipe().abortAllSubmissions();
			arg0.getUsbPipe().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void interDataEventOccurred(UsbPipeDataEvent event) {
		try {
			Util.showByteCode(event.getData(), "interDataEventOccurred");
			recvsem.acquire();
			recv.push(event.getData());
			recvsem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void inDataEventOccurred(UsbPipeDataEvent event) {
		Util.showByteCode(event.getData(), "inDataEventOccurred");
	}
	public void outDataEventOccurred(UsbPipeDataEvent event) {
		Util.showByteCode(event.getData(), "outDataEventOccurred");
		//Util.showByteCode(event.getData());
		//insem.release();
		//System.out.println("insem release");
	}

	public void run() {
		while(true) {
			try {
				if(initialized) {
					sendsem.acquire();
					if(send.size() > 0) {
						sendToUsb(send.removeLast());
					}
					sendsem.release();
					//receiveFromUsb();
				}
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void getPacket() {
		//byte[] packet = new byte[maxPacketSize];
		//sendToUsb(packet);
		receiveFromUsb();
	}
}
