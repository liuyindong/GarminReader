package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;

/**
 * UsbHub implementation.
 * <p>
 * This must be set up before use and/or connection to the topology tree.  To set up,
 * see {@link com.ibm.jusb.UsbDeviceImp UsbDeviceImp documentation}.  The number of ports may
 * be set in the constructor, or it will default to 1.  The number of ports can be dynamically
 * {@link #resize(int) resized} if needed.
 * <p>
 * The port numbering is 1-based, not 0-based.
 * @author Dan Streetman
 */
public class UsbHubImp extends UsbDeviceImp implements UsbHub
{
	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with a initial number of ports set to 1.
	 * The the number of ports is adjustable at runtime via the
	 * {@link #resize(int) resize} method.
	 */
	public UsbHubImp()
	{
		this( 1 );
		resizingAllowed = true;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with the specified number of ports.
	 * The number of ports is <i>not</i> adjustable at runtime via the
	 * {@link #resize(int) resize} method; it will throw UnsupportedOperationException.
	 * @param ports The initial number of ports.
	 */
	public UsbHubImp( int ports )
	{
		super();
		resize( ports );
		resizingAllowed = false;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with a initial number of ports set to 1.
	 * The the number of ports is adjustable at runtime via the
	 * {@link #resize(int) resize} method.
	 * @param desc This device's descriptor.
	 */
	public UsbHubImp(UsbDeviceDescriptor desc)
	{
		this( 1, desc );
		resizingAllowed = true;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with a initial number of ports set to 1.
	 * The the number of ports is adjustable at runtime via the
	 * {@link #resize(int) resize} method.
	 * @param device The platform device implementaiton.
	 */
	public UsbHubImp(UsbDeviceOsImp device)
	{
		this( 1, device );
		resizingAllowed = true;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with the specified number of ports.
	 * The number of ports is <i>not</i> adjustable at runtime via the
	 * {@link #resize(int) resize} method; it will throw UnsupportedOperationException.
	 * @param ports The initial number of ports.
	 * @param desc This device's descriptor.
	 */
	public UsbHubImp( int ports, UsbDeviceDescriptor desc )
	{
		super(desc);
		resize( ports );
		resizingAllowed = false;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with the specified number of ports.
	 * The number of ports is <i>not</i> adjustable at runtime via the
	 * {@link #resize(int) resize} method; it will throw UnsupportedOperationException.
	 * @param ports The initial number of ports.
	 * @param device The platform device implementation.
	 */
	public UsbHubImp( int ports, UsbDeviceOsImp device )
	{
		super(device);
		resize( ports );
		resizingAllowed = false;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with a initial number of ports set to 1.
	 * The the number of ports is adjustable at runtime via the
	 * {@link #resize(int) resize} method.
	 * @param desc This device's descriptor.
	 * @param device The platform device implementaiton.
	 */
	public UsbHubImp(UsbDeviceDescriptor desc, UsbDeviceOsImp device)
	{
		this( 1, desc, device );
		resizingAllowed = true;
	}

	/**
	 * Constructor.
	 * <p>
	 * This creates a hub with the specified number of ports.
	 * The number of ports is <i>not</i> adjustable at runtime via the
	 * {@link #resize(int) resize} method; it will throw UnsupportedOperationException.
	 * @param ports The initial number of ports.
	 * @param desc This device's descriptor.
	 * @param device The platform device implementation.
	 */
	public UsbHubImp( int ports, UsbDeviceDescriptor desc, UsbDeviceOsImp device )
	{
		super(desc, device);
		resize( ports );
		resizingAllowed = false;
	}

	//**************************************************************************
	// Public methods

	/**
	 * Resizes to the specified number of ports.
	 * <p>
	 * If resizing decreases the number of ports and there are
	 * devices attached to lost ports, this will remove only down to the
	 * port with a device attached.  The device(s) need to be removed before the port(s)!
	 * @param ports The total number of ports to resize to.
	 */
	public synchronized void resize( int ports ) throws UnsupportedOperationException
	{
		if (!resizingAllowed)
			throw new UnsupportedOperationException("Resizing is not allowed on this hub");

		int oldports = UsbUtil.unsignedInt( getNumberOfPorts() );

		if ( ports == oldports )
			return;

		if ( ports > USB_HUB_MAX_PORTS )
			ports = USB_HUB_MAX_PORTS;

		if ( ports < USB_HUB_MIN_PORTS )
			ports = USB_HUB_MIN_PORTS;

		if ( ports < oldports ) {
			/* Remove ports */
			for (int i=oldports; i>ports; i--) {
				if ( getUsbPortImp((byte)i).isUsbDeviceAttached() )
					return; /* Cannot remove in-use port */
				else
					portList.remove(i-1);
			}
		} else {
			/* Add ports */
			for (int i = oldports; i < ports; i++)
				portList.add( new UsbPortImp( this, (byte)(i+1) ) );
		}

	}

	/**
	 * Attach this device to this hub at the specified port.
	 * @param usbDeviceImp the UsbDeviceImp to attach.
	 * @param portNumber the number (1-based) of the port to attach the device to.
	 * @exception javax.usb.UsbException If the port is already occupied.
	 */
	public synchronized void addUsbDeviceImp( UsbDeviceImp usbDeviceImp, byte portNumber ) throws UsbException
	{
		if ( UsbUtil.unsignedInt( portNumber ) > UsbUtil.unsignedInt( getNumberOfPorts() ) )
			resize( portNumber );

		UsbPortImp usbPortImp = getUsbPortImp( portNumber );

		usbPortImp.attachUsbDeviceImp( usbDeviceImp );
	}

	/**
	 * Remove the device from this hub at the specified port.
	 * @param usbDeviceImp The UsbDeviceImp to remove.
	 * @param portNumber The number (1-based) of the port the device is attached to.
	 * @exception IllegalArgumentException if the device is not already attached
	 * to the port it is being removed from, or the port number is invalid.
	 */
	public synchronized void removeUsbDeviceImp( UsbDeviceImp usbDeviceImp, byte portNumber ) throws IllegalArgumentException
	{
		UsbPortImp usbPortImp = getUsbPortImp( portNumber );

		/* UsbPortImp does checking and may throw IllegalArgumentException */
		try {
			usbPortImp.detachUsbDeviceImp( usbDeviceImp );
		} catch ( NullPointerException npE ) {
			throw new IllegalArgumentException(USB_HUB_PORT_OUT_OF_RANGE + UsbUtil.unsignedInt(portNumber));
		}
	}

	/** @return true if this is a UsbHub and false otherwise */
	public boolean isUsbHub() { return true; }

	/** @return true if this is the virtual root hub */
	public boolean isRootUsbHub() { return false;  }

	/** @return the number of ports for this hub */
	public byte getNumberOfPorts() { return (byte)portList.size(); }

	/** @return an iteration of UsbPort objects attached to this hub */
	public List getUsbPorts() { return Collections.unmodifiableList(portList); }

	/**
	 * Get the specified port.
	 * @param number The number (1-based) of the port to get.
	 * @return The port with the specified number, or null.
	 */
	public UsbPort getUsbPort( byte number ) { return getUsbPortImp( number ); }

	/**
	 * Get the specified port.
	 * @param number The number (1-based) of the port to get.
	 * @return The port with the specified number, or null.
	 */
	public synchronized UsbPortImp getUsbPortImp( byte number )
	{
		int num = UsbUtil.unsignedInt(number);

		if (0 >= num || num > UsbUtil.unsignedInt(getNumberOfPorts()))
			return null;

		return (UsbPortImp)portList.get(num - 1);
	}

	/** @return an iteration of devices currently attached to this hub */
	public synchronized List getAttachedUsbDevices()
	{
		List attachedDevices = new ArrayList();

		for (int i=0; i<portList.size(); i++) {
			UsbPortImp portImp = (UsbPortImp)portList.get(i);
			UsbDeviceImp device = portImp.getUsbDeviceImp();
			if (null != device)
				attachedDevices.add(device);
		}

		return Collections.unmodifiableList(attachedDevices);
	}

	//**************************************************************************
	// Instance variables

	protected List portList = new LinkedList();

	protected boolean resizingAllowed = true;

	//**************************************************************************
	// Class constants

	public static final int USB_HUB_MIN_PORTS = 0x01;
	public static final int USB_HUB_MAX_PORTS = 0xff; /* USB 1.1 spec table 11.8 - max of 255 ports (1-based numbering) */

	private static final String USB_HUB_PORT_OUT_OF_RANGE = "No such port number on this hub : ";
}
