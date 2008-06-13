package net.nilosplace.GarminReader;


import java.util.Enumeration;

import javax.comm.CommPortIdentifier;

public class GarminReader {
	
	public GarminReader() {
	    Enumeration pList = CommPortIdentifier.getPortIdentifiers();
	    System.out.println(pList.hasMoreElements());
	    while (pList.hasMoreElements()) {
	      CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
	      System.out.print("Port " + cpi.getName() + " ");
	      if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	        System.out.println("is a Serial Port: " + cpi);
	      } else if (cpi.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
	        System.out.println("is a Parallel Port: " + cpi);
	      } else {
	        System.out.println("is an Unknown Port: " + cpi);
	      }
	    }
	  
	}

    public static void main(String[] args) {
    	GarminReader reader = new GarminReader();
    	
     }
}
