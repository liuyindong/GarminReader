package net.nilosplace.GarminReader.garmin.listeners;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import net.nilosplace.GarminReader.garmin.util.GarminUSBController;

public class OutListener implements UsbPipeListener {

	GarminUSBController garminUSBController;
	
	public OutListener(GarminUSBController garminUSBController) {
		this.garminUSBController = garminUSBController;
	}

	public void dataEventOccurred(UsbPipeDataEvent event) {
		//System.out.println("Out Data Event");
		garminUSBController.outDataEventOccurred(event);
	}

	public void errorEventOccurred(UsbPipeErrorEvent event) {
		garminUSBController.errorEventOccurred(event);
	}
}
