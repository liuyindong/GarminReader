package net.nilosplace.GarminReader.garmin.queue;

import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;
import javax.usb.UsbPipe;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import net.nilosplace.GarminReader.garmin.Packet;
import net.nilosplace.GarminReader.garmin.Util;

public class GarminRecievingQueue implements UsbPipeListener {

	private UsbPipe pipeIn;
	private int maxBufferSize = 64;
	
	public GarminRecievingQueue(UsbEndpoint pipeInEndP) throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException {
		pipeIn = pipeInEndP.getUsbPipe();
		pipeIn.open();
		pipeIn.addUsbPipeListener(this);
		send(new byte[maxBufferSize]);
	}
	
	public void send(byte[] packet) {
		try{
			
			if (!pipeIn.isOpen()){
				pipeIn.open();
			}
			Thread.sleep(2000);
			System.out.println("Sending Receiving Packet: ");
			//Util.showByteCode(packet);
			UsbIrp irpSend = pipeIn.createUsbIrp();
			irpSend.setComplete(false);
			//irpSend.setData(packet);
			pipeIn.syncSubmit(irpSend);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void dataEventOccurred(UsbPipeDataEvent arg0) {
		send(arg0.getData(), arg0.getActualLength());
		send(new byte[maxBufferSize]);
//	    try {
//			send(new byte[maxBufferSize]);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
	}

	public void errorEventOccurred(UsbPipeErrorEvent arg0) {
		System.out.println("Recieving Error: " + arg0.toString());
		System.out.println("Recieving Error: " + arg0.getUsbException().getLocalizedMessage());
		System.out.println("Recieving Error: " + arg0.getUsbException().getMessage());
	}
	
	private void send(byte[] packet, int size) {
		System.out.println("Incoming Packet: " + size);
		if(size > 0) Util.showByteCode(packet);
	}

	public Packet recieve() {
		return new Packet();
	}

}
