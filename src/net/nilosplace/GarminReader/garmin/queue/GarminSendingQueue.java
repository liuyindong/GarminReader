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

public class GarminSendingQueue implements UsbPipeListener {

	private UsbPipe pipeOut;
	
	public GarminSendingQueue(UsbEndpoint pipeOutEndP) throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException {
		pipeOut = pipeOutEndP.getUsbPipe();
		pipeOut.addUsbPipeListener(this);
		pipeOut.open();
	}

	public void send(byte[] packet) {
		try{
			if (!pipeOut.isOpen()){
				pipeOut.open();
			}
			System.out.println("Sending Sending Packet: ");
			Util.showByteCode(packet);
			UsbIrp irpSend = pipeOut.createUsbIrp();
			irpSend.setData(packet);
			pipeOut.syncSubmit(irpSend);
			
			//pipeOut.asyncSubmit(new byte[255]);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void dataEventOccurred(UsbPipeDataEvent arg0) {
		System.out.println("Sending Data Event: " + arg0);
		Util.showByteCode(arg0.getData());
	}

	public void errorEventOccurred(UsbPipeErrorEvent arg0) {
		System.out.println("Sending Error: " + arg0.toString());
		System.out.println("Sending Error: " + arg0.getUsbException().getLocalizedMessage());
		System.out.println("Sending Error: " + arg0.getUsbException().getMessage());
	}

}
