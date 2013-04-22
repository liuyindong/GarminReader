package ohgarmin;
import javax.usb.*;
//Klasse stellt mir ein USB-Device dar
//Sie beinhaltet einen Namen fr das Device
//+ die 3 Anschle (BulkIn, BulkOut & BulkInterrupt)
//+ das USBDevice - Objekt von der Api
public class Device {
	
	private String name = "";
	private int bulkIn = 0;
	private int bulkInterrupt = 0;
	private int bulkOut = 0;
	private UsbDevice device = null;
	
	public void setUsbDevice (UsbDevice device){
		this.device = device;
	}
	
	public UsbDevice getUsbDevice (){
		return (UsbDevice) this.device;
	}
	
	public void setName (String name){
		this.name = name;
	}
	
	public String getName (){
		return this.name;
	}
	
	public void setBulkIn (int bulkIn){
		this.bulkIn = bulkIn;
	}
	
	public void setBulkOut (int bulkOut){
		this.bulkOut = bulkOut;
	}
	
	public void setBulkInterrupt (int bulkInterrupt){
		this.bulkInterrupt = bulkInterrupt;
	}
	
	public int getBulkIn (){
		return this.bulkIn;
	}
	
	public int getBulkOut(){
		return this.bulkOut;
	}
	
	public int getBulkInterrupt(){
		return this.bulkInterrupt;
	}
	
	public String toString (){
		return this.name;
	}
	
	
}
