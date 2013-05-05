package net.nilosplace.GarminReader.garmin.util;

import java.util.concurrent.Semaphore;

import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;
import javax.usb.UsbPipe;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;

import net.nilosplace.GarminReader.garmin.listeners.InListener;
import net.nilosplace.GarminReader.garmin.listeners.InterListener;
import net.nilosplace.GarminReader.garmin.listeners.OutListener;

public class GarminUSBController {

	protected UsbIrp irpInSend;
	protected UsbIrp irpOutSend;
	protected UsbIrp irpInterSend;
	protected Semaphore insem = new Semaphore(1);
	protected Semaphore exsem = new Semaphore(1);
	protected int maxBufferSize = 64;
	protected byte[] returnedPacket;
	
	protected UsbPipe pipeIn;
	protected UsbPipe pipeOut;
	protected UsbPipe pipeInter;
	
	public GarminUSBController(UsbEndpoint pipeInEndP, UsbEndpoint pipeOutEndP, UsbEndpoint pipeInterruptP) throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException {

		pipeIn = pipeInEndP.getUsbPipe();
		//pipeIn.addUsbPipeListener(new InListener(this));
		
		pipeOut = pipeOutEndP.getUsbPipe();
		//pipeOut.addUsbPipeListener(new OutListener(this));

		pipeInter = pipeInterruptP.getUsbPipe();
		//pipeInter.addUsbPipeListener(new InterListener(this));

	}
	
	public byte[] send(Packet packet) {
		byte[] ret = null;
		try {
			System.out.println("Aquiring exsem");
			exsem.acquire();
			System.out.println("exsem Aquired");
			sendToUsb(packet);
			returnedPacket = receiveFromUsb();

			if(returnedPacket[4] == -1) {
				receiveFromUsb();
				receiveFromUsb();
			}

			ret = new byte[returnedPacket.length];
			ret = returnedPacket.clone();

			exsem.release();
			return ret;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendToUsb(Packet packet) {
		sendToUsb(packet.createPacket());
	}
	
	private void sendToUsb(byte[] packet) {
		try {
			System.out.println("Sending to Out USB");
			returnedPacket = null;
			if (!pipeOut.isOpen()){
				pipeOut.open();
			}
			if(irpOutSend == null) {
				irpOutSend = pipeOut.createUsbIrp();
			}
			//System.out.println("Sending Packet: " + this.getClass().getName());
			//Util.showByteCode(packet);
			irpOutSend.setComplete(false);
			irpOutSend.setData(packet);
			irpOutSend.setActualLength(packet.length);
			//if(sync) pipe.syncSubmit(irpSend);
			//else pipe.asyncSubmit(irpSend);
			System.out.println("Sending Packet");
			Util.showByteCode(packet);
			pipeOut.syncSubmit(irpOutSend);
			System.out.println("Packet Sent");
			Util.showByteCode(packet);
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
			System.out.println("Sending to USB");
			if (!pipeInter.isOpen()){
				pipeInter.open();
			}
			if(irpInterSend == null) {
				irpInterSend = pipeInter.createUsbIrp();
			}

			//System.out.println("Sending Packet: " + this.getClass().getName());
			//Util.showByteCode(packet);
			irpInterSend.setComplete(false);
			irpInterSend.setData(packet);
			irpInterSend.setActualLength(packet.length);
			//if(sync) pipe.syncSubmit(irpSend);
			//else pipe.asyncSubmit(irpSend);
			System.out.println("Sending Packet");
			Util.showByteCode(packet);
			pipeInter.syncSubmit(irpInterSend);
			System.out.println("Packet Sent");
			Util.showByteCode(packet);
			if(irpInterSend.isUsbException()) {
				System.out.println("USB Exception:");
			}
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			close();
			System.exit(0);
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
		System.out.println("Error: " + arg0.getUsbException().getLocalizedMessage());
		System.out.println("Error: " + arg0.getUsbException().getMessage());
	}

	public void interDataEventOccurred(UsbPipeDataEvent event) {
		//System.out.println("Received Original Packet: " + this.getClass().getName());
		//System.out.println(event.getData().length);
		returnedPacket = new byte[event.getData().length];
		returnedPacket = event.getData();
		System.out.println("Returned Interrupt Packet:");
		Util.showByteCode(returnedPacket);
		insem.release();
	}
	public void inDataEventOccurred(UsbPipeDataEvent event) {
		
	}
	public void outDataEventOccurred(UsbPipeDataEvent event) {
		System.out.println("Returned Output Packet");
		Util.showByteCode(event.getData());
		insem.release();
		System.out.println("insem release");
	}

}
