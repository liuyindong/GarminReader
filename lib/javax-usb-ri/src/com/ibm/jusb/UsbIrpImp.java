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
import javax.usb.event.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;

/**
 * UsbIrp implementation.
 * <p>
 * If the user provided their own UsbIrp implementation, then the UsbPipeImp will 'wrap' their
 * implementation with this UsbIrpImp by {@link #setUsbIrp(UsbIrp) setting} the local
 * {@link #getUsbIrp() UsbIrp}.  If this has a local UsbIrp when it is
 * {@link #complete() complete}, this will set the proper fields on the wrapped UsbIrp.
 * @author Dan Streetman
 */
public class UsbIrpImp extends DefaultUsbIrp implements UsbIrp
{
	/** Constructor. */
	public UsbIrpImp()
	{
		super();
	}

	/**
	 * Constructor.
	 * @param irp The UsbIrp this should wrap.
	 */
	public UsbIrpImp(UsbIrp irp)
	{
		this();
		setUsbIrp(irp);
	}

	/**
	 * Complete this submission.
	 * <p>
	 * The order of events must be as follows:
	 * <ol>
	 * <li>If there is a 'wrapped' UsbIrp, its fields must be set, and it should be completed.</li>
	 * <li>This UsbIrpImp should be completed.</li>
	 * <li>The UsbIrpImpListener should be notified (which will fire events on the device or pipe).</li>
	 * </ol>
	 */
	public void complete()
	{
		/* Some implementations *cough*4690*cough* ignore Short Packet exceptions. */
		checkShortPacketException();

		/* Complete wrapped UsbIrp fields before completing ourself */
		completeUsbIrp();

		/* Set this UsbIrpImp as complete and notify waiting Threads. */
		super.complete();

		/* Notify UsbIrpImpListener (should be either a UsbDeviceImp or UsbPipeImp) */
		try { getUsbIrpImpListener().usbIrpImpComplete(this); }
		catch ( NullPointerException npE ) { }
	}

	/** If there is a wrapped UsbIrp, set its fields and complete it. */
	protected void completeUsbIrp()
	{
//FIXME - the user's UsbIrp methods could block or generate Exception/Error which will cause problems
		try {
			UsbIrp irp = getUsbIrp();
			irp.setUsbException(getUsbException());
			irp.setActualLength(getActualLength());
			irp.complete();
		} catch ( NullPointerException npE ) { }
	}

	/**
	 * Set the UsbIrp to wrap.
	 * @param irp The UsbIrp.
	 */
	public void setUsbIrp(UsbIrp irp)
	{
		usbIrp = irp;
		setData(irp.getData(),irp.getOffset(),irp.getLength());
		setAcceptShortPacket(irp.getAcceptShortPacket());
		setUsbException(irp.getUsbException());
		setComplete(irp.isComplete());
	}

	/**
	 * If this UsbIrpImp has a wrapped UsbIrp.
	 * @return If there is a wrapped UsbIrp.
	 */
	public boolean hasUsbIrp() { return null != getUsbIrp(); }

	/**
	 * Get the UsbIrp this is wrapping.
	 * @return The UsbIrp or null.
	 */
	public UsbIrp getUsbIrp() { return usbIrp; }

	/**
	 * Set the UsbIrpImpListener.
	 * <p>
	 * If there is already a Listener, this will clobber it!
	 */
	public synchronized void setUsbIrpImpListener(UsbIrpImpListener listener)
	{
		//FIXME - log if there is already a listener?
		usbIrpImpListener = listener;
	}

	/**
	 * Get the UsbIrpImpListener.
	 * @return The UsbIrpImpListener.
	 */
	public UsbIrpImpListener getUsbIrpImpListener() { return usbIrpImpListener; }

	/**
	 * Check the specified UsbIrp.
	 * <p>
	 * This may be used to check the validity of an UsbIrp.
	 * This will throw an IllegalArgumentException if the UsbIrp
	 * does not behave as specified in the UsbIrp interface documentation.
	 * This will throw an UsbException if the UsbIrp is in a state not
	 * ready for submission, such as being complete or having a UsbException.
	 * @exception IllegalArgumentException If the UsbIrp is not valid.
	 * @exception UsbException If the UsbIrp is not ready for submission.
	 */
	public static void checkUsbIrp(UsbIrp irp) throws IllegalArgumentException,UsbException
	{
		int datalen, offset, length;

		if (null == irp.getData())
			throw new IllegalArgumentException("UsbIrp data cannot be null.");

		datalen = irp.getData().length;
		offset = irp.getOffset();
		length = irp.getLength();

		if (0 > offset)
			throw new IllegalArgumentException("UsbIrp offset ("+offset+") cannot be negative.");

		if (0 > length)
			throw new IllegalArgumentException("UsbIrp length ("+length+") cannot be negative.");

		if (datalen < (offset + length))
			throw new UsbException("Data buffer (length "+datalen+") is smaller than offset ("+offset+") + length ("+length+") = "+(offset+length));

		if (irp.isComplete())
			throw new UsbException("UsbIrp cannot be used while isComplete() is true.");

		if (irp.isUsbException())
			throw new UsbException("UsbIrp cannot be used while isUsbException() is true.");
	}

	/**
	 * If createShortPacketException is true, this will check for a short packet condition.
	 * <p>
	 * If there is a short packet condition but no exception is set, this will create and set one.
	 */
	protected void checkShortPacketException()
	{
		if (!createShortPacketException || getAcceptShortPacket())
			return;

		if ((getLength() > getActualLength()) && !isUsbException())
			setUsbException(new UsbShortPacketException("Short Packet condition was detected by javax.usb common implementation."));
	}

	/**
	 * This should be set by the UsbPipeImp or UsbDeviceImp if this UsbIrpImp should check/set ShortPacketExceptions.
	 * <p>
	 * If this is set to true, the {@link #checkShortPacketException() checkShortPacketException} method
	 * will create and set a UsbShortPacketException during {#link #complete() complete}, if appropriate.
	 * @param setting The setting.
	 */
	public void setCreateShortPacketException(boolean setting) { createShortPacketException = setting; }
	

	protected UsbIrp usbIrp = null;

	protected UsbIrpImpListener usbIrpImpListener = null;

	protected boolean createShortPacketException = false;

	public static interface UsbIrpImpListener extends EventListener
	{ public void usbIrpImpComplete(UsbIrpImp usbIrpImp); }

}
