package net.nilosplace.GarminReader.garmin;

import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;

import net.nilosplace.GarminReader.garmin.util.GarminUSBController;
import net.nilosplace.GarminReader.garmin.util.Packet;
import net.nilosplace.GarminReader.garmin.util.Util;

public class GarminApi {
	
	private GarminUSBController controller = null;

	public GarminApi() {
		try {
			controller = new GarminUSBController();
			controller.start();
		} catch (UsbNotActiveException e) {
			e.printStackTrace();
		} catch (UsbNotClaimedException e) {
			e.printStackTrace();
		} catch (UsbDisconnectedException e) {
			e.printStackTrace();
		} catch (UsbException e) {
			e.printStackTrace();
		}
	}

	public void startSession() {
		controller.send(new Packet(Packet.Pid_Start_Session));
	}
		
	public void getAllProductProtocolData() {
		controller.send(new Packet(Packet.Pid_Product_Rqst));
	}

	public void close() {
		controller.close();
	}

	public void startPVTData() {
		byte[] data = Util.shortTobyte(Packet.Cmnd_Start_Pvt_Data);
		Packet p = new Packet(Packet.Pid_Command_Data, data);
		controller.send(p);
	}
	
	public void stopPVTData() {
		byte[] data = Util.shortTobyte(Packet.Cmnd_Stop_Pvt_Data);
		Packet p = new Packet(Packet.Pid_Command_Data, data);
		controller.send(p);
	}

	public void getPacket() {
		controller.getPacket();
	}
}
