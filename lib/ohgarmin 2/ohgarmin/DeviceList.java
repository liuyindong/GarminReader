package ohgarmin;
import java.util.Vector;
import javax.usb.*;
//Die Klasse speichert alle Devices die gefunden worden sind
public class DeviceList {
	private Vector<Device> list = new Vector<Device>();
	
	//Methode um ein device hinzuzufgen
	public void addDevice(Device device){
		list.add(device);
	}
	
	//Methode um ein Gert per Index zu enterfenen aus der Liste
	public void removeDevice(int i){
		list.removeElementAt(i);
	}
	
	//Listet alle Devices auf
	public void listDevices (){
		for (int x = 0; x < list.size(); x++){
			System.out.println(x+": "+list.elementAt(x).toString());
		}
	}
	
	public UsbDevice getUsbDevice (int index){
		for (int x = 0; x < list.size(); x++){
			if (index == x){
				Device geraet = (Device) list.elementAt(x);
				return geraet.getUsbDevice();
			}
		}
		return null;
	}
	
}
