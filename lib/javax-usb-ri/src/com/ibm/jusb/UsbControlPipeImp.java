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

import com.ibm.jusb.os.*;

/**
 * UsbPipe platform-independent implementation for Control-type pipes.
 * @author Dan Streetman
 */
public class UsbControlPipeImp extends UsbPipeImp implements UsbPipe
{
	/** Constructor. */
	public UsbControlPipeImp() { super(); }

	/**
	 * Constructor.
	 * @param ep The UsbEndpointImp.
	 */
	public UsbControlPipeImp( UsbEndpointImp ep ) { super(ep); }

	/**
	 * Constructor.
	 * @param pipe The platform-dependent pipe implementation.
	 */
	public UsbControlPipeImp( UsbControlPipeOsImp pipe ) { super(pipe); }

	/**
	 * Constructor.
	 * @param ep The UsbEndpointImp.
	 * @param pipe The platform-dependent pipe implementation.
	 */
	public UsbControlPipeImp( UsbEndpointImp ep, UsbControlPipeOsImp pipe ) { super(ep,pipe); }

	/**
	 * Control pipes cannot handle raw byte[] submissions.
	 * <p>
	 * Since Control pipes require a setup packet, raw byte[]s are disallowed.
	 * This will throw UsbException.
	 * @exception UsbException Raw byte[]s cannot be used on Control pipes.
	 */
	public int syncSubmit( byte[] data ) throws UsbException
	{
		throw new UsbException("Control pipes require a setup packet, so raw byte[] submission cannot be used.");
	}

	/**
	 * Control pipes cannot handle raw byte[] submissions.
	 * <p>
	 * Since Control pipes require a setup packet, raw byte[]s are disallowed.
	 * This will throw UsbException.
	 * @exception UsbException Raw byte[]s cannot be used on Control pipes.
	 */
	public UsbIrp asyncSubmit( byte[] data ) throws UsbException
	{
		throw new UsbException("Control pipes require a setup packet, so raw byte[] submission cannot be used.");
	}

	/**
	 * Convert a UsbControlIrp to UsbControlIrpImp.
	 * <p>
	 * If the UsbIrp is not a UsbControlIrp, a UsbException is thrown.
	 * This does not use the superclass method.
	 * @param irp The UsbControlIrp to convert.
	 */
	protected UsbIrpImp usbIrpToUsbIrpImp(UsbIrp irp) throws UsbException
	{
		UsbControlIrp usbControlIrp = null;
		try {
			usbControlIrp = (UsbControlIrp)irp;
		} catch ( ClassCastException ccE ) {
			throw new UsbException("Control pipes require a setup packet per submission, so only UsbControlIrps can be submitted.");
		}

		UsbControlIrpImp usbControlIrpImp = null;
		try {
			usbControlIrpImp = (UsbControlIrpImp)usbControlIrp;
		} catch ( ClassCastException ccE ) {
			usbControlIrpImp = new UsbControlIrpImp(usbControlIrp);
		}

		usbControlIrpImp.setUsbDeviceImp(getUsbEndpointImp().getUsbInterfaceImp().getUsbConfigurationImp().getUsbDeviceImp());
		setupUsbIrpImp(usbControlIrpImp);

		return usbControlIrpImp;
	}
}

