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
 * Default implementation for UsbControlPipeOsImp.
 * <p>
 * This is identical to DefaultUsbPipeOsImp except all the methods require
 * UsbControlIrpImps, not UsbIrpImps.  This should be driven by a {@link com.ibm.jusb.UsbControlPipeImp UsbControlPipeImp},
 * not a normal {@link com.ibm.jusb.UsbPipeImp UsbPipeImp}.
 * @author Dan Streetman
 */
public class DefaultUsbControlPipeOsImp extends DefaultUsbPipeOsImp implements UsbControlPipeOsImp
{
	/** Constructor. */
	public DefaultUsbControlPipeOsImp() { super(); }

	/**
	 * Constructor.
	 * @param open The String to use in UsbExceptions thrown in {@link #open() open()}.
	 * @param submit The String to use in UsbExceptions thrown in submit methods.
	 */
	public DefaultUsbControlPipeOsImp(String open, String submit) { super(open,submit); }

	/**
	 * Constructor.
	 * <p>
	 * If this is true, opening is allowed.
	 * @param open If this should allow opening.
	 */
	public DefaultUsbControlPipeOsImp(boolean open) { super(open); }

	/**
	 * Synchronously submit a UsbControlIrpImp.
	 * <p>
	 * This casts the UsbIrpImp to a UsbControlIrpImp and uses the
	 * {@link #syncSubmit(UsbControlIrpImp) syncSubmit(UsbControlIrpImp)} method.
	 * @param irp The UsbControlIrpImp to submit.
	 * @exception UsbException If {@link #syncSubmit(UsbControlIrpImp) syncSubmit(UsbControlIrpImp)} throws a UsbException.
	 */
	public void syncSubmit( UsbIrpImp irp ) throws UsbException,ClassCastException
	{
		try {
			syncSubmit((UsbControlIrpImp)irp);
		} catch ( ClassCastException ccE ) {
			try {
				syncSubmit(new UsbControlIrpImp((UsbControlIrp)irp));
			} catch ( ClassCastException ccE2 ) {
				throw new UsbException("Control pipes can only handle UsbControlIrps.");
			}
		}
	}

	/**
	 * Asynchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * This casts the UsbIrpImp to a UsbControlIrpImp and uses the
	 * {@link #syncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)} method.
	 * @param irp The UsbControlIrpImp to submit.
	 * @exception UsbException If {@link #asyncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)} throws a UsbException.
	 */
	public void asyncSubmit( UsbIrpImp irp ) throws UsbException,ClassCastException
	{
		try {
			asyncSubmit((UsbControlIrpImp)irp);
		} catch ( ClassCastException ccE ) {
			try {
				asyncSubmit(new UsbControlIrpImp((UsbControlIrp)irp));
			} catch ( ClassCastException ccE2 ) {
				throw new UsbException("Control pipes can only handle UsbControlIrps.");
			}
		}
	}

	/**
	 * Synchronously submits this UsbControlIrpImp to the platform implementation.
	 * <p>
	 * This uses {@link #asyncSubmit(UsbControlIrpImp) asyncSubmit(UsbControlIrpImp)}.
	 * @param irp the UsbControlIrpImp to use for this submission.
	 * @exception UsbException If the data transfer was unsuccessful.
	 */
	public void syncSubmit( UsbControlIrpImp irp ) throws UsbException
	{
		asyncSubmit(irp);

		irp.waitUntilComplete();

		if (irp.isUsbException())
			throw irp.getUsbException();
	}

	/**
	 * Asynchronously submits this UsbControlIrpImp to the platform implementation.
	 * <p>
	 * By default, this throws UsbException with the String defined by {@link #getSubmitString() getSubmitString}.
	 * The implementation should override (at least) this method.
	 * @param irp the UsbControlIrpImp to use for this submission.
	 * @exception UsbException If the initial submission was unsuccessful.
	 */
	public void asyncSubmit( UsbControlIrpImp irp ) throws UsbException
	{
		throw new UsbException(getSubmitString());
	}

}
