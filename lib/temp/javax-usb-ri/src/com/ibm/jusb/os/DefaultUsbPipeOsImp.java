package com.ibm.jusb.os;

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

import com.ibm.jusb.*;

/**
 * Default UsbPipeOsImp implementation.
 * <p>
 * This is an optional class that handles all methods.  Those methods may be overridden
 * by the implementation if desired.	The implementation does not have to extend this abstract class.
 * The default behavior is to throw an UsbException for all methods.
 * @author Dan Streetman
 */
public class DefaultUsbPipeOsImp implements UsbPipeOsImp
{
	/**
	 * Constructor.
	 * <p>
	 * This defaults to not allow opening (or use, obviously).
	 */
	public DefaultUsbPipeOsImp() { }

	/**
	 * Constructor.
	 * @param open The String to use in UsbExceptions thrown in {@link #open() open()}.
	 * @param submit The String to use in UsbExceptions thrown in submit methods.
	 */
	public DefaultUsbPipeOsImp(String open, String submit)
	{
		openString = open;
		submitString = submit;
	}

	/**
	 * Constructor.
	 * <p>
	 * If this is true, opening is allowed.
	 * @param open If this should allow opening.
	 */
	public DefaultUsbPipeOsImp(boolean open)
	{
		allowOpen = open;
	}

	/**
	 * Open this pipe.
	 * <p>
	 * If {@link #allowOpen() allowOpen} is true, this does nothing, otherwise
	 * this throws UsbException using the String specified by {@link #getOpenString() getOpenString}.
	 * The implementation can override this method if appropriate.
	 * @exception UsbException If {@link #allowOpen() allowOpen} is false.
	 */
	public void open() throws UsbException
	{
		if (!allowOpen())
			throw new UsbException(getOpenString());
	}

	/**
	 * Close this pipe.
	 * <p>
	 * By default, this does nothing.  The implementation can override this method if appropriate.
	 */
	public void close() { }

	/**
	 * Synchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * This is implemented using {@link #asyncSubmit(UsbIrpImp) asyncSubmit(UsbIrpImp)}.
	 * @param irp the UsbIrpImp to use for this submission.
	 * @exception javax.usb.UsbException If the data transfer was unsuccessful.
	 */
	public void syncSubmit( UsbIrpImp irp ) throws UsbException
	{
		asyncSubmit(irp);

		irp.waitUntilComplete();

		if (irp.isUsbException())
			throw irp.getUsbException();
	}

	/**
	 * Asynchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * By default, this throws UsbException with the String defined by {@link #getSubmitString() getSubmitString}.
	 * The implementation should override (at least) this method.
	 * @param irp the UsbIrpImp to use for this submission.
	 * @exception javax.usb.UsbException If the initial submission was unsuccessful.
	 */
	public void asyncSubmit( UsbIrpImp irp ) throws UsbException
	{
		UsbException uE = new UsbException(getSubmitString());
		irp.setUsbException(uE);
		irp.complete();
		throw uE;
	}

	/**
	 * Synchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * This is implemented using {@link #syncSubmit(UsbIrpImp) syncSubmit(UsbIrpImp)}.
	 * If any UsbException occurrs, it is thrown immediately and no further UsbIrpImps are submitted nor modified.
	 * @param list the UsbIrpImps to use for this submission.
	 * @exception javax.usb.UsbException If one of the UsbIrpImps failed.
	 */
	public void syncSubmit( List list ) throws UsbException
	{
		for (int i=0; i<list.size(); i++)
			syncSubmit((UsbIrpImp)list.get(i));
	}

	/**
	 * Asynchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * This is implemented using {@link #asyncSubmit(UsbIrpImp) asyncSubmit(UsbIrpImp)}.
	 * If any UsbException occurrs, it is thrown immediately and no further UsbIrpImps are submitted or modified.
	 * @param list The List of UsbIrpImps.
	 * @exception javax.usb.UsbException If one of the UsbIrpImps was not accepted by the implementation.
	 */
	public void asyncSubmit( List list ) throws UsbException
	{
		for (int i=0; i<list.size(); i++)
			asyncSubmit((UsbIrpImp)list.get(i));
	}

	/**
	 * Stop all submissions in progress.
	 * <p>
	 * By default, this does nothing.  The implementation should override this method.
	 */
	public void abortAllSubmissions() { }

	/**
	 * If this allows opening.
	 * @return If this allows opening.
	 */
	protected boolean allowOpen() { return allowOpen; }

	/**
	 * Get the String to use in UsbExceptions thrown in {@link #open() open()}.
	 * @return The String to use in open() UsbExceptions.
	 */
	protected String getOpenString() { return openString; }

	/**
	 * Get the String to use in UsbExceptions thrown in all submit methods.
	 * @return The String to use in submission UsbExceptions.
	 */
	protected String getSubmitString() { return submitString; }

	protected boolean allowOpen = true;
	protected String openString = OPEN_STRING;
	protected String submitString = SUBMIT_STRING;

	public static final String OPEN_STRING = "Cannot open this pipe.";
	public static final String SUBMIT_STRING = "Cannot use this pipe.";

	public static final String HOST_CONTROLLER_OPEN_STRING = "Cannot open a host controller pipe.";
	public static final String HOST_CONTROLLER_SUBMIT_STRING = "Cannot use a host controller pipe.";
}
