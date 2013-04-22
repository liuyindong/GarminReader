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
 * Interface for Platform-specific implementation of control-type UsbPipe.
 * <p>
 * This is identical to UsbPipeOsImp with added methods using UsbControlPipeOsImp parameters.
 * <p>
 * If this is driven by a UsbControlPipeImp, it will always pass only UsbControlIrpImps,
 * however it is not guaranteed to pass them to the UsbControlIrpImp-parameter methods.  The
 * implementation of this should handle UsbControlIrpImps being passed (as UsbIrpImps) to
 * the UsbIrpImp-parameter methods.
 * <p>
 * If this is driven by a normal UsbPipeImp, it may pass normal UsbIrpImps (i.e. not UsbControlIrpImps)
 * and so the implementation of this must handle throwing a UsbException for non-UsbControlIrpImps.
 * @author Dan Streetman
 */
public interface UsbControlPipeOsImp extends UsbPipeOsImp
{
	/**
	 * Synchronously submits this UsbControlIrpImp to the platform implementation.
	 * <p>
	 * This is identical to
	 * {@link com.ibm.jusb.os.UsbPipeOsImp#syncSubmit(UsbIrpImp) UsbPipeOsImp.syncSubmit(UsbIrpImp)}
	 * except the parameter is a UsbControlIrpImp.
	 * @param irp the UsbControlIrpImp to use for this submission.
	 * @exception UsbException If the data transfer was unsuccessful.
	 */
	public void syncSubmit( UsbControlIrpImp irp ) throws UsbException;

	/**
	 * Asynchronously submits this UsbControlIrpImp to the platform implementation.
	 * This is identical to
	 * {@link com.ibm.jusb.os.UsbPipeOsImp#asyncSubmit(UsbIrpImp) UsbPipeOsImp.asyncSubmit(UsbIrpImp)}
	 * except the parameter is a UsbControlIrpImp.
	 * <p>
	 * @param irp the UsbControlIrpImp to use for this submission
	 * @exception UsbException If the UsbControlIrpImp was not accepted by the implementation.
	 */
	public void asyncSubmit( UsbControlIrpImp irp ) throws UsbException;
}
