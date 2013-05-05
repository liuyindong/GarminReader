package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

/**
 * UsbEndpoint implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The UsbInterfaceImp must be set either in the constructor or by its {@link #setUsbInterfaceImp(UsbInterfaceImp) setter}.</li>
 * <li>The UsbEndpointDescriptor must be set either in the constructor or by its {@link #setUsbEndpointDescriptor(UsbEndpointDescriptor) setter}.</li>
 * <li>The UsbPipeImp must be set by its {@link #setUsbPipeImp(UsbPipeImp) setter}.
 *     Note the UsbPipeImp will automatically do this inside its {@link com.ibm.jusb.UsbPipeImp#setUsbEndpointImp(UsbEndpointImp) setUsbEndpointImp} method.</li>
 * </ul>
 * @author Dan Streetman
 */
public class UsbEndpointImp implements UsbEndpoint
{
	/**
	 * Constructor.
	 * @param iface The parent interface.
	 * @param desc This endpoint's descriptor.
	 */
	public UsbEndpointImp( UsbInterfaceImp iface, UsbEndpointDescriptor desc )
	{
		setUsbInterfaceImp(iface);
		setUsbEndpointDescriptor(desc);
	}

	//**************************************************************************
    // Public methods

    /**
     * @return direction of this endpoint (i.e. in [from device to host] or out
     * [from host to device])
     */
    public byte getDirection() { return (byte)(getUsbEndpointDescriptor().bEndpointAddress() & UsbConst.ENDPOINT_DIRECTION_MASK); }

    /** @return this endpoint's type */
    public byte getType() { return (byte)(getUsbEndpointDescriptor().bmAttributes() & UsbConst.ENDPOINT_TYPE_MASK); }

    /** @return The UsbInterface */
    public UsbInterface getUsbInterface() { return getUsbInterfaceImp(); }

	/** @return The UsbInterfaceImp */
	public UsbInterfaceImp getUsbInterfaceImp() { return usbInterfaceImp; }

	/**
	 * Set the UsbInterfaceImp.
	 * <p>
	 * This will also add this to the parent UsbInterfaceImp.
	 * @param iface The interface
	 */
    public void setUsbInterfaceImp( UsbInterfaceImp iface )
	{
		usbInterfaceImp = iface;

		if (null != iface)
			iface.addUsbEndpointImp(this);
	}

	/** @return The UsbPipe */
    public UsbPipe getUsbPipe() { return getUsbPipeImp(); }

	/** @return The UsbPipeImp */
	public UsbPipeImp getUsbPipeImp() { return usbPipeImp; }

	/** @param pipe The pipe */
	public void setUsbPipeImp(UsbPipeImp pipe) { usbPipeImp = pipe; }

	/** @return the endpoint descriptor for this endpoint */
	public UsbEndpointDescriptor getUsbEndpointDescriptor() { return usbEndpointDescriptor; }

	/** @param desc the endpoint descriptor */
	public void setUsbEndpointDescriptor( UsbEndpointDescriptor desc ) { usbEndpointDescriptor = desc; }

	//**************************************************************************
    // Package methods

	/** Disconnect this and close its pipe. */
	void disconnect() { getUsbPipeImp().disconnect(); }

	/**
	 * Check if this device is disconnected.
	 * @exception UsbDisconnectedException If the device is disconnected.
	 */
	void checkDisconnected() { getUsbInterfaceImp().checkDisconnected(); }

	/** @return If this device is disconnected. */
	boolean isDisconnected() { return getUsbInterfaceImp().isDisconnected(); }

	//**************************************************************************
    // Instance variables

    private UsbInterfaceImp usbInterfaceImp = null;
	private UsbEndpointDescriptor usbEndpointDescriptor = null;
    private UsbPipeImp usbPipeImp = null;

}
