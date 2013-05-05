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
 * Default simple implementation of UsbInterfacePolicy.
 * @author Dan Streetman
 */
public class DefaultUsbInterfacePolicy implements UsbInterfacePolicy
{
	/**
	 * If the claim should be forced.
	 * <p>
	 * This will try to forcibly claim the UsbInterface.
	 * This is only intended as a flag to the implementation
	 * to try everything possible to allow a successful claim.
	 * The implementation may try to override any other driver(s)
	 * that have the interface claimed.
	 * <p>
	 * The implementation is not required to use this flag.
	 * <p>
	 * <strong>WARNING</strong>: This should <i>only</i> be used
	 * if you are <i>absolutely sure</i> you want to drive the
	 * interface.
	 * @param usbInterface The UsbInterface being claimed.
	 * @return If the interface should be forcibly claimed.
	 */
	public boolean forceClaim(UsbInterface usbInterface) { return false; }

}
