package net.nilosplace.GarminReader.garmin.listeners;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import net.nilosplace.GarminReader.garmin.util.GarminUSBController;

public class InterListener implements UsbPipeListener {

	GarminUSBController garminUSBController;
	
	public InterListener(GarminUSBController garminUSBController) {
		this.garminUSBController = garminUSBController;
	}

	public void dataEventOccurred(UsbPipeDataEvent event) {
		garminUSBController.interDataEventOccurred(event);
	}

	public void errorEventOccurred(UsbPipeErrorEvent event) {
		garminUSBController.errorEventOccurred(event);
	}
}
