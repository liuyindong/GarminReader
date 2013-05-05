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

/**
 * Interface for Platform-specific implementation of UsbDevice.
 * <p>
 * All methods are synchronized in the Platform-Independent layer; the
 * implementation does not need to make them Thread-safe.
 * @author Dan Streetman
 */
public interface UsbDeviceOsImp
{
	/**
	 * Synchronously submit a UsbControlIrpImp.
	 * <p>
	 * This will block until the UsbControlIrpImp has completed.
	 * The implementation should throw the UsbException if one occurs.
	 * The implementation must perform all actions as specified in the
	 * {@link com.ibm.jusb.UsbControlIrpImp UsbControlIrpImp documentation}.
	 * @param controlUsbIrpImp The UsbControlIrpImp.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public void syncSubmit(UsbControlIrpImp controlUsbIrpImp) throws UsbException;

	/**
	 * Asynchronously submit a UsbControlIrpImp.
	 * <p>
	 * This should return as soon as possible.	The implementation must perform
	 * all actions specified in the
	 * {@link com.ibm.jusb.UsbControlIrpImp UsbControlIrpImp's documentation}.  In addition,
	 * if the UsbControlIrpImp is not accepted for processing (i.e. before returning from
	 * this method), a UsbException should be thrown.  Otherwise (i.e. after returning
	 * from this method) no UsbException can be thrown (but should be set on the UsbControlIrpImp).
	 * @param controlUsbIrpImp The UsbControlIrpImp.
	 * @throws UsbException If the submission was not accepted by the implementation.
	 */
	public void asyncSubmit(UsbControlIrpImp controlUsbIrpImp) throws UsbException;

	/**
	 * Synchronously submit a List of UsbControlIrpImps.
	 * <p>
	 * All items in the List will be UsbControlIrpImps.  Every UsbControlIrpImp
	 * should be handled as if passed via
	 * {@link #syncSubmit(UsbControlIrpImp) single UsbControlIrpImp syncSubmit},
	 * with the only difference being an error for one of the UsbControlIrpImps does
	 * not require the implementation to throw a UsbException for the entire List.
	 * The implementation may, at its option, continue processing UsbControlIrpImps.
	 * In either case (return from the method or throw an UsbException),
	 * all UsbIrpImps must be handled as specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp documentation}, even if they are not processed.
	 * <p>
	 * Note that the implementation may call each UsbControlIrpImp's
	 * {@link com.ibm.jusb.UsbControlIrpImp#complete() complete} method as the UsbControlIrpImp
	 * completes or after processing all UsbControlIrpImps in the list.
	 * @param list The List.
	 * @throws UsbException If the one (or more) submissions are unsuccessful (optional).
	 */
	public void syncSubmit(List list) throws UsbException;

	/**
	 * Asynchronously submit a List of UsbControlIrpImps.
	 * <p>
	 * All items in the List will be UsbControlIrpImps.  Every UsbControlIrpImp
	 * should be handled as if passed via
	 * {@link #asyncSubmit(UsbControlIrpImp) single UsbControlIrpImp asyncSubmit},
	 * with the only difference being an error for one of the UsbControlIrpImps does
	 * not require the implementation to throw a UsbException for the entire List.
	 * The implementation may, at its option, continue processing UsbControlIrpImps.
	 * In either case (return from the method or throw an UsbException),
	 * all UsbIrpImps must be handled as specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp documentation}, even if they are not processed.
	 * <p>
	 * Note that the implementation may call each UsbControlIrpImp's
	 * {@link com.ibm.jusb.UsbControlIrpImp#complete() complete} method as the UsbControlIrpImp
	 * completes or after processing all UsbControlIrpImps in the list.
	 * @param list The List.
	 * @throws UsbException If the one (or more) submissions are unsuccessful (optional).
	 */
	public void asyncSubmit(List list) throws UsbException;

}
