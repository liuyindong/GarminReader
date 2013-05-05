package com.ibm.jusb.os;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.*;

import com.ibm.jusb.*;
import com.ibm.jusb.util.*;

/**
 * Default UsbDeviceOsImp implementation.
 * <p>
 * This is an optional default implementation that handles all methods.  Those
 * methods may be overridden by the implementation if desired.  The implementation
 * is not required to extend this class.  All methods are implemented using the
 * {@link #asyncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)} method; this method,
 * at a minimum, must be implemented in order to provide any actual functionality.
 * <p>
 * The default action for this class is to throw a UsbException for all submission methods.
 * @author Dan Streetman
 */
public class DefaultUsbDeviceOsImp implements UsbDeviceOsImp
{
	/**
	 * Constructor.
	 */
	public DefaultUsbDeviceOsImp() { }

	/**
	 * Constructor.
	 * @param submitString The String to be used in UsbExceptions thrown from a submission method.
	 */
	public DefaultUsbDeviceOsImp(String submitString) { this.submitString = submitString; }

	/**
	 * Synchronously submit a UsbControlIrpImp.
	 * <p>
	 * This method is implemented using {@link #asyncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)}.
	 * @param usbControlIrpImp The UsbControlIrpImp.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public void syncSubmit(UsbControlIrpImp usbControlIrpImp) throws UsbException
	{
		asyncSubmit(usbControlIrpImp);

		usbControlIrpImp.waitUntilComplete();

		if (usbControlIrpImp.isUsbException())
			throw usbControlIrpImp.getUsbException();
	}

	/**
	 * Asynchronously submit a UsbControlIrpImp.
	 * <p>
	 * This throws a UsbException with the specified {@link #getSubmitString() string}.
	 * The implementation should override (at least) this method.
	 * @param usbControlIrpImp The UsbControlIrpImp.
	 * @exception UsbException If the submission is unsucessful.
	 */
	public void asyncSubmit(UsbControlIrpImp usbControlIrpImp) throws UsbException
	{
		UsbException uE = new UsbException(getSubmitString());
		usbControlIrpImp.setUsbException(uE);
		usbControlIrpImp.complete();
		throw uE;
	}

	/**
	 * Synchronously submit a List of UsbControlIrpImps.
	 * <p>
	 * This method is implemented using {@link #syncSubmit(UsbControlIrpImp) syncSubmit(UsbControlIrpImp)}.
	 * If an UsbException occurrs, it is thrown immediately and any remaining UsbControlIrpImps are not submitted nor modified.
	 * @param list The List.
	 */
	public void syncSubmit(List list) throws UsbException
	{
		for (int i=0; i<list.size(); i++)
			syncSubmit((UsbControlIrpImp)list.get(i));
	}

	/**
	 * Asynchronously submit a List of UsbControlIrpImps.
	 * <p>
	 * This method is implemented using {@link #asyncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)}.
	 * If an UsbException occurrs, it is thrown immediately and any remaining UsbControlIrpImps are not submitted nor modified.
	 * @param list The List.
	 */
	public void asyncSubmit(List list) throws UsbException
	{
		for (int i=0; i<list.size(); i++)
			asyncSubmit((UsbControlIrpImp)list.get(i));
	}

	/** @return The String to be used in UsbExceptions thrown from submit methods. */
	protected String getSubmitString() { return submitString; }

	protected RunnableManager runnableManager = null;
	protected String submitString = SUBMIT_STRING;

	public static final String SUBMIT_STRING = "Cannot use the default control pipe for this device.";

	public static final String HOST_CONTROLLER_SUBMIT_STRING = "Cannot use the default control pipe on a host controller device.";
}
